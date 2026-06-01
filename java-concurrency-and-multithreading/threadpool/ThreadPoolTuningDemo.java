package threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPoolTuningDemo — How thread pools work and how to tune them correctly.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * Your API is slow. Thread dump shows 200 threads all WAITING.
 * Or: tasks are being REJECTED with RejectedExecutionException.
 * Or: you used Executors.newCachedThreadPool() and now have 10,000 threads.
 *
 * ROOT CAUSE: Misconfigured thread pool.
 *
 * HOW ThreadPoolExecutor ACTUALLY WORKS (most devs get this wrong):
 * ─────────────────────────────────────────────────────────────────────
 *  1. Tasks arrive → assigned to a thread if corePoolSize not reached
 *  2. corePoolSize reached → tasks go to the queue
 *  3. Queue FULL → new threads created up to maximumPoolSize
 *  4. maximumPoolSize reached AND queue full → RejectedExecutionHandler fires
 *
 *  COMMON MISTAKE: "Threads grow to max first, then queue fills"
 *  REALITY:        "Queue fills first, THEN threads grow beyond core"
 *
 * THREAD POOL SIZING FORMULAS:
 * ─────────────────────────────────────────────────────────────────────
 *  CPU-BOUND tasks:   threads = CPU cores + 1
 *  IO-BOUND tasks:    threads = CPU cores × (1 + wait_time / compute_time)
 *                     (e.g. 8 cores, 10x more wait than compute = 88 threads)
 *
 * REJECTION POLICIES:
 * ─────────────────────────────────────────────────────────────────────
 *  AbortPolicy        → throw RejectedExecutionException (default)
 *  CallerRunsPolicy   → caller thread executes the task (natural backpressure) ✓
 *  DiscardPolicy      → silently drop the task (dangerous!)
 *  DiscardOldestPolicy→ drop the oldest queued task, retry new one
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class ThreadPoolTuningDemo {

    // ── 1: Correct pool for CPU-bound work ───────────────────────────────
    static ExecutorService cpuBoundPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available CPU cores: " + cores);
        System.out.println("CPU-bound pool size: " + (cores + 1));

        return new ThreadPoolExecutor(
            cores + 1,                          // corePoolSize
            cores + 1,                          // maximumPoolSize (same = fixed size)
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100),     // bounded queue — important!
            new ThreadFactory() {
                private final AtomicInteger n = new AtomicInteger(1);
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "cpu-worker-" + n.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // backpressure instead of reject
        );
    }

    // ── 2: Correct pool for IO-bound work ────────────────────────────────
    static ExecutorService ioBoundPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        // Assume: 90% wait time, 10% compute → ratio = 9
        int ioRatio    = 9;
        int poolSize   = cores * (1 + ioRatio); // e.g. 8 cores → 80 threads
        System.out.println("IO-bound pool size: " + poolSize);

        return new ThreadPoolExecutor(
            poolSize, poolSize,
            60L, TimeUnit.SECONDS,         // idle threads expire after 60s
            new LinkedBlockingQueue<>(500), // larger queue for IO
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    // ── 3: The dangerous anti-patterns to avoid ──────────────────────────
    static void showAntiPatterns() {
        System.out.println("\n--- Thread Pool Anti-Patterns ---");

        System.out.println("""
            
            ❌ Executors.newCachedThreadPool()
               → Unbounded threads. Under load: 10,000+ threads = OutOfMemoryError
               → Use only for very short-lived tasks with low concurrency
            
            ❌ Executors.newFixedThreadPool(100) with default queue
               → Default queue is LinkedBlockingQueue with Integer.MAX_VALUE capacity
               → Tasks queue up to 2 billion items → OutOfMemoryError
               → Always use bounded queue with ThreadPoolExecutor directly
            
            ❌ AbortPolicy (default) without handling RejectedExecutionException
               → Tasks silently dropped under load, no backpressure
               → Use CallerRunsPolicy for natural backpressure
            
            ✅ ThreadPoolExecutor with:
               - Explicit core + max pool sizes
               - Bounded queue (LinkedBlockingQueue with capacity)
               - Named thread factory (for debugging thread dumps)
               - CallerRunsPolicy (graceful backpressure)
            """);
    }

    // ── 4: Monitor a running pool ─────────────────────────────────────────
    static void monitorPool(ThreadPoolExecutor pool, String label) {
        System.out.printf("  [%s] Active: %d | Pool size: %d | Queue: %d | Completed: %d%n",
            label,
            pool.getActiveCount(),
            pool.getPoolSize(),
            pool.getQueue().size(),
            pool.getCompletedTaskCount()
        );
    }

    // ── Main ──────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread Pool Tuning Demo ===\n");

        ExecutorService pool = cpuBoundPool();
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) pool;

        System.out.println("\n--- Submitting 20 tasks to CPU-bound pool ---");
        AtomicInteger completed = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(20);

        for (int i = 1; i <= 20; i++) {
            final int taskId = i;
            pool.submit(() -> {
                try {
                    // Simulate CPU work
                    Thread.sleep(50);
                    completed.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Monitor pool while tasks run
        for (int i = 0; i < 3; i++) {
            Thread.sleep(30);
            monitorPool(tpe, "CPU-pool");
        }

        latch.await(5, TimeUnit.SECONDS);
        monitorPool(tpe, "CPU-pool");
        System.out.println("Completed tasks: " + completed.get());

        pool.shutdown();
        showAntiPatterns();

        System.out.println("=== Key Insight ===");
        System.out.println("Queue fills BEFORE threads grow past corePoolSize.");
        System.out.println("Use VirtualThreads (Java 21) for IO-bound workloads — no pool sizing needed!");
    }
}
