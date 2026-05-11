
# Executors & Thread Pools – Managing Concurrency at Scale

Manually creating threads does not scale.

In real‑world Java systems:
✅ Threads are expensive  
✅ Uncontrolled thread creation causes failures  
✅ Thread lifecycle must be managed carefully  

This is why Java provides **Executors and Thread Pools**.

Strong backend engineers **never create threads per task**.

---

## Why Not Create Threads Manually?

Example (bad practice):

```java
new Thread(() -> handleRequest()).start();
````

Problems:
❌ Unbounded thread creation  
❌ High memory usage  
❌ Context‑switch overhead  
❌ No control over concurrency

This approach fails under load.

***

## Core Idea of Thread Pools

> **A thread pool reuses a fixed number of threads to execute many tasks.**

Instead of:

*   Creating a thread per task

We:
✅ Submit tasks  
✅ Threads pick tasks from a queue

This gives:
✅ Controlled concurrency  
✅ Better performance  
✅ Predictable resource usage

***

## Executor Framework (High Level)

Java provides the **Executor Framework** via:

    java.util.concurrent

Key abstractions:
✅ Executor  
✅ ExecutorService  
✅ ThreadPoolExecutor

This framework separates:

> **Task submission from task execution.**

***

## Executor Interface

```java
Executor executor = ...
executor.execute(task);
```

Executor:
✅ Knows *how* to run tasks  
✅ Caller doesn’t care about threads

This abstraction is critical for scalable design.

***

## ExecutorService Interface

> **ExecutorService extends Executor and adds lifecycle management.**

Capabilities:
✅ Task submission  
✅ Result handling  
✅ Graceful shutdown

Key methods:

*   `submit()`
*   `shutdown()`
*   `shutdownNow()`

***

## Creating Thread Pools (Common Types)

Java provides factory methods via `Executors`.

***

## 1️⃣ Fixed Thread Pool

```java
ExecutorService pool = Executors.newFixedThreadPool(10);
```

Characteristics:
✅ Fixed number of threads  
✅ Tasks queued when threads are busy

Used for:

*   Stable workloads
*   CPU‑bound tasks (with proper sizing)

***

## 2️⃣ Cached Thread Pool

```java
ExecutorService pool = Executors.newCachedThreadPool();
```

Characteristics:
✅ Creates threads as needed  
✅ Reuses idle threads

❌ Can grow unbounded

Use with caution — easy to overload system.

***

## 3️⃣ Single Thread Executor

```java
ExecutorService pool = Executors.newSingleThreadExecutor();
```

Characteristics:
✅ Only one thread  
✅ Tasks executed sequentially

Used for:

*   Serial task execution
*   Guaranteeing order

***

## 4️⃣ Scheduled Thread Pool

```java
ScheduledExecutorService scheduler =
    Executors.newScheduledThreadPool(2);
```

Used for:
✅ Delayed tasks  
✅ Periodic tasks

Better alternative to `Timer`.

***

## Submitting Tasks

### Runnable (No Result)

```java
executor.execute(() -> doWork());
```

or

```java
executor.submit(() -> doWork());
```

***

### Callable (Returns Result)

```java
Future<Integer> future =
    executor.submit(() -> compute());
```

`Callable`:
✅ Returns a value  
✅ Can throw checked exceptions

***

## Future (High Level)

> **`Future` represents the result of an asynchronous computation.**

```java
Integer result = future.get(); // blocks
```

Important:
✅ `get()` blocks  
✅ Exceptions are wrapped

Improper use can cause thread starvation.

***

## Thread Pool Internals (Conceptual)

A thread pool consists of:
✅ Worker threads  
✅ Task queue  
✅ Rejection policy

Tasks flow like this:

    Submit Task → Queue → Worker Thread → Execute

Understanding this helps debug performance issues.

***

## Thread Pool Size (Interview Favorite)

Rule of thumb:

*   **CPU‑bound**:
        threads ≈ number of cores

*   **I/O‑bound**:
        threads > cores (waiting on I/O)

Wrong sizing:
❌ Too small → underutilization  
❌ Too large → context switching & memory pressure

***

## ThreadPoolExecutor (Advanced)

```java
new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    timeUnit,
    workQueue
);
```

Allows:
✅ Full control  
✅ Custom sizing  
✅ Custom queues

Used in high‑performance systems.

***

## Task Rejection Policies

When queue is full:

✅ AbortPolicy – throws exception  
✅ CallerRunsPolicy – caller executes  
✅ DiscardPolicy – silently drops task

Interview insight:

> **Rejection is better than uncontrolled overload.**

***

## Shutting Down Executors (Very Important)

Proper shutdown is mandatory.

```java
executor.shutdown();      // graceful
executor.shutdownNow();   // forceful
```

Never forget shutdown:
❌ Resource leaks  
❌ JVM won’t exit

***

## Common Executor Mistakes (Interview Red Flags)

❌ Creating new executor per request  
❌ Forgetting shutdown  
❌ Blocking inside thread pool tasks  
❌ Unbounded pools without limits

***

## Executors and Concurrency Safety

Important insight:

> **Thread pools manage threads, not data safety.**

Even with Executors:
❌ Shared mutable state still needs synchronization

Thread pools solve execution management, not race conditions.

***

## Interview‑Ready Explanation (Use This)

> “In production systems, tasks are executed using thread pools via the Executor framework. This allows controlled concurrency, thread reuse, and predictable resource usage. Different executors serve different workloads, and proper sizing and shutdown are critical for correctness and performance.”

Clear. Calm. Senior‑level ✅

***

## Key Takeaways

✅ Do not create threads manually  
✅ Thread pools control concurrency  
✅ Executors separate task submission from execution  
✅ Pool size matters  
✅ Proper shutdown is essential

> **Concurrency at scale requires managing threads, not creating them.**
