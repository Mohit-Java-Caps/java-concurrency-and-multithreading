package deadlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DeadlockDemo — Demonstrates deadlock creation, detection, and 3 prevention strategies.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * Production system freezes. CPU drops to 0%. No errors in logs.
 * All threads appear "BLOCKED" in thread dump.
 * This is a deadlock.
 *
 * WHAT IS DEADLOCK:
 * ─────────────────────────────────────────────────────────────────────
 * Thread A holds Lock-1, waiting for Lock-2.
 * Thread B holds Lock-2, waiting for Lock-1.
 * Neither can proceed. System freezes indefinitely.
 *
 * FOUR CONDITIONS FOR DEADLOCK (all must be true):
 * ─────────────────────────────────────────────────────────────────────
 *  1. Mutual Exclusion  — resource held by only one thread
 *  2. Hold and Wait     — thread holds a lock and waits for another
 *  3. No Preemption     — lock can't be taken away forcefully
 *  4. Circular Wait     — A waits for B, B waits for A (cycle)
 *
 * PREVENTION STRATEGIES (demonstrated below):
 * ─────────────────────────────────────────────────────────────────────
 *  1. Lock Ordering      — always acquire locks in the same global order
 *  2. tryLock + timeout  — give up if can't acquire within time limit
 *  3. Single lock        — redesign to need only one lock at a time
 *
 * HOW TO DETECT IN PRODUCTION:
 * ─────────────────────────────────────────────────────────────────────
 *  kill -3 <pid>                         → print thread dump to stdout
 *  jstack <pid>                          → detailed thread dump
 *  ThreadMXBean.findDeadlockedThreads()  → programmatic detection
 *  VisualVM / JProfiler                  → visual deadlock graph
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class DeadlockDemo {

    private static final Object LOCK_ACCOUNT_A = new Object();
    private static final Object LOCK_ACCOUNT_B = new Object();

    // ── Part 1: Create a deadlock (for demo — don't run in prod!) ────────
    static void demonstrateDeadlock() {
        System.out.println("--- Deadlock Demo (will freeze — press Ctrl+C after observing) ---");

        Thread thread1 = new Thread(() -> {
            synchronized (LOCK_ACCOUNT_A) {
                System.out.println("Thread-1: Acquired LOCK_ACCOUNT_A, waiting for LOCK_ACCOUNT_B...");
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
                synchronized (LOCK_ACCOUNT_B) {
                    System.out.println("Thread-1: Acquired both locks — transfer complete");
                }
            }
        }, "Thread-TransferAtoB");

        Thread thread2 = new Thread(() -> {
            synchronized (LOCK_ACCOUNT_B) {   // <— opposite order = deadlock!
                System.out.println("Thread-2: Acquired LOCK_ACCOUNT_B, waiting for LOCK_ACCOUNT_A...");
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
                synchronized (LOCK_ACCOUNT_A) {
                    System.out.println("Thread-2: Acquired both locks — transfer complete");
                }
            }
        }, "Thread-TransferBtoA");

        thread1.start();
        thread2.start();

        // Detect deadlock programmatically
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        java.lang.management.ThreadMXBean bean = java.lang.management.ManagementFactory.getThreadMXBean();
        long[] deadlocked = bean.findDeadlockedThreads();
        if (deadlocked != null) {
            System.out.println("\n!!! DEADLOCK DETECTED by ThreadMXBean !!!");
            System.out.println("Deadlocked thread IDs: " + java.util.Arrays.toString(deadlocked));
        }

        thread1.interrupt();
        thread2.interrupt();
    }

    // ── Part 2: Fix 1 — Lock Ordering ────────────────────────────────────
    /**
     * PREVENTION STRATEGY 1: Always acquire locks in a consistent global order.
     * Both threads acquire LOCK_ACCOUNT_A first, then LOCK_ACCOUNT_B.
     * Thread-2 waits at LOCK_ACCOUNT_A — no circular wait — no deadlock.
     */
    static void fixWithLockOrdering() throws InterruptedException {
        System.out.println("\n--- Fix 1: Lock Ordering (always acquire in same order) ---");

        Thread thread1 = new Thread(() -> {
            synchronized (LOCK_ACCOUNT_A) {         // same order
                synchronized (LOCK_ACCOUNT_B) {     // same order
                    System.out.println("Thread-1: Transfer A→B complete (no deadlock)");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (LOCK_ACCOUNT_A) {         // same order as Thread-1 ← KEY FIX
                synchronized (LOCK_ACCOUNT_B) {     // same order
                    System.out.println("Thread-2: Transfer B→A complete (no deadlock)");
                }
            }
        });

        thread1.start(); thread2.start();
        thread1.join();  thread2.join();
    }

    // ── Part 3: Fix 2 — tryLock with timeout ─────────────────────────────
    /**
     * PREVENTION STRATEGY 2: Use ReentrantLock.tryLock(timeout).
     * If a thread can't acquire all needed locks within the timeout,
     * it releases what it holds and retries later — breaking the deadlock cycle.
     */
    static void fixWithTryLock() throws InterruptedException {
        System.out.println("\n--- Fix 2: tryLock with timeout ---");

        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();

        Runnable transferTask = () -> {
            String name = Thread.currentThread().getName();
            for (int attempt = 1; attempt <= 3; attempt++) {
                boolean acquiredA = false, acquiredB = false;
                try {
                    acquiredA = lockA.tryLock(100, TimeUnit.MILLISECONDS);
                    acquiredB = lockB.tryLock(100, TimeUnit.MILLISECONDS);

                    if (acquiredA && acquiredB) {
                        System.out.println(name + ": Transfer complete on attempt " + attempt);
                        return;
                    } else {
                        System.out.println(name + ": Could not acquire all locks, retrying (attempt " + attempt + ")");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    // Always release in finally — avoid holding partial locks
                    if (acquiredB) lockB.unlock();
                    if (acquiredA) lockA.unlock();
                }

                // Back-off before retry — helps one thread win
                try { Thread.sleep((long)(Math.random() * 50)); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        };

        Thread t1 = new Thread(transferTask, "Thread-1");
        Thread t2 = new Thread(transferTask, "Thread-2");
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

    // ── Main ──────────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Deadlock: Detection and Prevention ===\n");

        // Uncomment to see the actual deadlock (will freeze):
        // demonstrateDeadlock();

        fixWithLockOrdering();
        fixWithTryLock();

        System.out.println("\n=== Summary ===");
        System.out.println("Deadlock prevention strategies:");
        System.out.println("  1. Lock ordering    — simplest, most reliable");
        System.out.println("  2. tryLock + timeout — flexible, handles complex cases");
        System.out.println("  3. Single lock      — redesign to avoid multiple lock acquisition");
        System.out.println("\nDetection in production: jstack <pid> | grep -A 20 'BLOCKED'");
    }
}
