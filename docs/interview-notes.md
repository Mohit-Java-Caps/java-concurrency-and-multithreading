

# 🧵 Java Concurrency Interview Questions & Answers

*(Backend & Senior‑Level Focus)*

***

## 1️⃣ What is concurrency in Java?

**Answer (spoken style):**

> Concurrency is the ability of a Java program to execute multiple tasks that overlap in time.  
> It usually involves multiple threads accessing shared resources and requires careful coordination to ensure correctness.

***

## 2️⃣ Concurrency vs Parallelism — what’s the difference?

**Answer:**

> Concurrency is about managing multiple tasks at once, while parallelism is about executing tasks simultaneously on multiple cores.  
> Java supports both, but concurrency is a design concept and parallelism is an execution detail.

***

## 3️⃣ What is a thread?

**Answer:**

> A thread is the smallest unit of execution within a Java process.  
> All threads in a JVM share the same heap memory but have their own stack.  
> Concurrency issues arise because threads share memory.

***

## 4️⃣ How do you create threads in Java?

**Answer:**

> Threads can be created by extending the `Thread` class, implementing `Runnable`, or using executors.  
> In real systems, executors and thread pools are preferred over manual thread creation.

***

## 5️⃣ Why should you not create threads manually in production?

**Answer:**

> Manual thread creation doesn’t scale.  
> It leads to unbounded threads, high memory usage, context switching overhead, and unpredictable failures under load.  
> Thread pools allow controlled concurrency.

***

## 6️⃣ What is the thread lifecycle in Java?

**Answer:**

> A Java thread goes through states like NEW, RUNNABLE, BLOCKED, WAITING, TIMED\_WAITING, and TERMINATED.  
> Understanding these states helps diagnose deadlocks, performance issues, and blocked threads.

***

## 7️⃣ What is a race condition?

**Answer:**

> A race condition occurs when multiple threads access shared data and the final result depends on the timing of execution.  
> It usually happens when read‑modify‑write operations are not synchronized.

***

## 8️⃣ How does Java prevent race conditions?

**Answer:**

> Java prevents race conditions using synchronization mechanisms like `synchronized`, explicit locks, and atomic classes.  
> The goal is to protect shared mutable state.

***

## 9️⃣ What does the `synchronized` keyword do?

**Answer:**

> `synchronized` provides mutual exclusion and memory visibility.  
> Only one thread can execute a synchronized block or method at a time, and updates are visible to other threads when the lock is released.

***

## 🔟 What is a critical section?

**Answer:**

> A critical section is a block of code that accesses shared mutable data and must not be executed by more than one thread at the same time.

***

## 1️⃣1️⃣ What problems can excessive synchronization cause?

**Answer:**

> Excessive synchronization causes lock contention, reduced throughput, poor scalability, and increased latency.  
> Concurrency design is about finding the right balance.

***

## 1️⃣2️⃣ What is a deadlock?

**Answer:**

> A deadlock occurs when two or more threads wait forever for locks held by each other.  
> It’s a design issue caused by inconsistent lock ordering or nested locks.

***

## 1️⃣3️⃣ How do you prevent deadlocks?

**Answer:**

> Deadlocks can be prevented by acquiring locks in a consistent order, minimizing nested locks, using timeouts with `tryLock`, and keeping critical sections small.

***

## 1️⃣4️⃣ What is memory visibility?

**Answer:**

> Memory visibility refers to when a write by one thread becomes visible to another thread.  
> Without proper synchronization, threads may see stale values due to caching and reordering.

***

## 1️⃣5️⃣ What does `volatile` do?

**Answer:**

> `volatile` ensures memory visibility and prevents instruction reordering around the variable.  
> It guarantees that writes by one thread are immediately visible to others.

***

## 1️⃣6️⃣ What does `volatile` NOT do?

**Answer:**

> `volatile` does not provide atomicity or mutual exclusion.  
> It cannot be used for counters or compound operations like increment‑then‑update.

***

## 1️⃣7️⃣ When should `volatile` be used?

**Answer:**

> `volatile` is best used for state flags, configuration values, and one‑writer‑many‑reader scenarios, such as shutdown signals.

***

## 1️⃣8️⃣ What is the Java Memory Model (JMM)?

**Answer:**

> The Java Memory Model defines the visibility and ordering guarantees for memory operations between threads.  
> It explains why synchronization and happens‑before relationships are required.

***

## 1️⃣9️⃣ What is happens‑before?

**Answer:**

> Happens‑before is a guarantee that the effects of one action are visible to another.  
> If there is no happens‑before relationship, visibility is not guaranteed.

***

## 2️⃣0️⃣ How do `synchronized` and `volatile` relate to happens‑before?

**Answer:**

> Exiting a synchronized block happens‑before entering it again, and a write to a volatile variable happens‑before every subsequent read.  
> These rules define their visibility guarantees.

***

## 2️⃣1️⃣ What is the Executor framework?

**Answer:**

> The Executor framework decouples task submission from execution.  
> It manages thread lifecycle, reuse, and task scheduling using thread pools.

***

## 2️⃣2️⃣ Why are thread pools important?

**Answer:**

> Thread pools prevent uncontrolled thread creation, improve performance through reuse, and allow predictable resource usage under load.

***

## 2️⃣3️⃣ Difference between CPU‑bound and I/O‑bound tasks?

**Answer:**

> CPU‑bound tasks should use thread counts close to the number of cores, while I/O‑bound tasks can use more threads since they spend time waiting.

***

## 2️⃣4️⃣ What is thread starvation?

**Answer:**

> Thread starvation occurs when threads never get CPU time or lock access due to poor scheduling, unfair locks, or blocked thread pools.

***

## 2️⃣5️⃣ What are common concurrency bug patterns?

**Answer:**

> The most common patterns are race conditions, visibility bugs, deadlocks, livelocks, thread starvation, and blocking calls inside thread pools.

***

## ✅ Interview‑Ready Summary (Strong Closing Line)

> **Java concurrency is about correctness under parallel execution.  
> Understanding threads, synchronization, memory visibility, happens‑before, and thread pools allows engineers to build reliable, scalable systems.**

