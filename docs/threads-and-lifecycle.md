
# Threads & Lifecycle – How Java Executes Concurrent Work

Threads are the **basic unit of concurrency** in Java.

Understanding threads deeply is critical because:
- Every web request runs on a thread
- Thread mismanagement causes performance issues
- Many concurrency bugs originate at this level

This phase explains **what threads are, how they are created, and how they progress through their lifecycle**.

---

## What Is a Thread?

> **A thread is a lightweight unit of execution within a process.**

In Java:
- A process = JVM instance
- Multiple threads = concurrent execution paths inside the JVM

Threads share:
✅ Heap memory  
✅ Open resources  

Threads do not share:
❌ Stack memory  

---

## Why Threads Matter in Backend Systems

Backend systems are inherently multi‑threaded:
- Web servers handle requests in parallel
- Background jobs run asynchronously
- Thread pools execute tasks concurrently

If you don’t understand threads:
❌ Bugs will appear under load  
❌ Systems will behave unpredictably  

---

## Main Thread in Java

Every Java program starts with a **main thread**:

```java
public static void main(String[] args) {
    // runs on main thread
}
````

All other threads are created **explicitly or implicitly**.

***

## Creating Threads in Java

### 1️⃣ Extending `Thread` Class

```java
class MyThread extends Thread {
    public void run() {
        // task logic
    }
}

new MyThread().start();
```

✅ Simple  
❌ Inherits Thread (limits flexibility)

***

### 2️⃣ Implementing `Runnable` (Preferred)

```java
class MyTask implements Runnable {
    public void run() {
        // task logic
    }
}

new Thread(new MyTask()).start();
```

✅ Better design  
✅ Separates task from execution

***

### 3️⃣ Using Lambdas (Modern Java)

```java
new Thread(() -> {
    // task logic
}).start();
```

Used frequently in simple cases.

***

## Important Rule (Interview Favorite)

> **Never call `run()` directly. Always call `start()`.**

*   `run()` → normal method call (no new thread)
*   `start()` → creates a new thread

Calling `run()` does **not** create concurrency.

***

## Thread Lifecycle States (Critical Topic)

A Java thread goes through **well‑defined states**.

***

## 1️⃣ NEW

*   Thread object created
*   `start()` not called yet

```java
Thread t = new Thread(task); // NEW
```

Thread exists but does not execute.

***

## 2️⃣ RUNNABLE

*   Thread is ready to run
*   Or running on CPU

```java
t.start(); // RUNNABLE
```

Important:

> **RUNNABLE does NOT mean currently executing** — it means *eligible to execute*.

***

## 3️⃣ BLOCKED

*   Thread is waiting to acquire a monitor lock

Example:

*   Thread tries to enter a synchronized block
*   Lock is held by another thread

Thread cannot proceed until lock is released.

***

## 4️⃣ WAITING

*   Thread waits indefinitely for another thread’s action

Examples:

```java
Object.wait();
Thread.join();
```

Thread remains idle until explicitly notified.

***

## 5️⃣ TIMED\_WAITING

*   Thread waits for a fixed duration

Examples:

```java
Thread.sleep(1000);
object.wait(1000);
```

Thread resumes automatically after timeout.

***

## 6️⃣ TERMINATED

*   Thread execution completed
*   Cannot be restarted

Once terminated:
❌ Thread is unusable

***

## Thread Lifecycle Summary

    NEW → RUNNABLE → (BLOCKED | WAITING | TIMED_WAITING) → RUNNABLE → TERMINATED

Threads may move between states multiple times.

***

## Thread Scheduling (High Level)

Important insight:

> **Thread scheduling is controlled by the OS, not Java.**

Java:
✅ Requests execution  
OS:
✅ Decides when a thread runs

You cannot rely on execution order.

***

## Thread Priority (Often Misunderstood)

Java allows thread priorities:

```java
thread.setPriority(Thread.MAX_PRIORITY);
```

But:
❌ Not a guarantee  
❌ OS may ignore it

Never build logic assuming priority order.

***

## Daemon vs User Threads

### User Threads

✅ Keep JVM alive  
✅ Used for application logic

***

### Daemon Threads

❌ JVM does not wait for them  
✅ Used for background tasks (GC, monitoring)

Example:

```java
thread.setDaemon(true);
```

Daemon threads stop when JVM shuts down.

***

## Thread Interruption (Very Important)

> **Interruption is a cooperative mechanism, not forced termination.**

Interrupting a thread:

```java
thread.interrupt();
```

Thread must:
✅ Check interruption status  
✅ Handle it intentionally

Ignoring interrupts causes resource leaks.

***

## Common Threading Mistakes

❌ Creating too many threads  
❌ Ignoring thread lifecycle  
❌ Blocking threads unnecessarily  
❌ Assuming execution order  
❌ Forgetting interruption handling

These mistakes often appear in production bugs.

***

## Interview‑Ready Explanation (Use This)

> “Threads are the basic unit of concurrency in Java. They move through states like NEW, RUNNABLE, BLOCKED, WAITING, and TERMINATED. Thread scheduling is controlled by the OS, not the JVM. Proper thread lifecycle management and interruption handling are essential for building reliable concurrent systems.”

Clear. Confident. Senior‑level ✅

***

## Key Takeaways

✅ Threads execute concurrent logic  
✅ `start()` creates a new thread  
✅ Lifecycle states explain thread behavior  
✅ Scheduling is non‑deterministic  
✅ Correct handling prevents bugs

> **Understanding threads is the first step to writing correct concurrent code.**
