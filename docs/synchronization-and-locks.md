
# Synchronization & Locks – Protecting Shared Data

Synchronization is the mechanism that **prevents multiple threads from corrupting shared state**.

Most concurrency bugs happen because:
❌ Shared data is accessed by multiple threads  
❌ Access is not properly synchronized  

This phase explains **how Java enforces mutual exclusion**, when to use locks, and what trade‑offs are involved.

---

## Why Synchronization Is Needed

In concurrent programs:
- Multiple threads run independently
- Threads share heap memory
- Operations are not atomic by default

Without synchronization:
❌ Race conditions  
❌ Lost updates  
❌ Inconsistent state  

---

## Core Problem: Race Condition

> **A race condition occurs when the outcome depends on the timing of thread execution.**

Example (conceptual):
- Two threads read the same value
- Both modify it
- Both write it back

Result:
❌ One update is lost  

Race conditions are **logic bugs**, not syntax errors.

---

## Mutual Exclusion (Key Concept)

> **Only one thread should access a critical section at a time.**

Java achieves this using:
✅ Intrinsic locks (`synchronized`)  
✅ Explicit locks (`Lock` interface)  

---

## Critical Section

> **A critical section is a block of code that accesses shared mutable data.**

Goal:
✅ Allow only one thread at a time  
✅ Keep critical sections small  

---

## `synchronized` Keyword (Intrinsic Lock)

`synchronized` ensures:
✅ Mutual exclusion  
✅ Memory visibility  

Only one thread can hold a given monitor lock at a time.

---

## `synchronized` Method

```java
public synchronized void increment() {
    count++;
}
````

Equivalent to:

*   Locking on `this`
*   Entire method is a critical section

***

## `synchronized` Block

```java
synchronized (lockObject) {
    count++;
}
```

Advantages:
✅ Finer‑grained control  
✅ Smaller critical sections

Best practice:

> Prefer synchronized blocks over synchronized methods.

***

## What Does `synchronized` Actually Do?

When a thread enters a synchronized block:
✅ Acquires a lock  
✅ Flushes local memory  
✅ Ensures visibility of latest values

When exiting:
✅ Releases lock  
✅ Writes changes back to main memory

This provides **visibility guarantees**, not just locking.

***

## Intrinsic Locks (Monitors)

Every Java object has:
✅ Exactly one intrinsic lock (monitor)

You can synchronize on:

*   `this`
*   Any object reference
*   Class object (`ClassName.class`)

***

## Class‑Level Locking

```java
public static synchronized void method() {
    // lock on ClassName.class
}
```

Used when:
✅ Protecting static shared data

Instance locks do NOT protect static variables.

***

## Lock Contention

> **Lock contention occurs when multiple threads compete for the same lock.**

High contention leads to:
❌ Reduced throughput  
❌ Increased latency

Design goal:
✅ Minimize contention  
✅ Keep locks short‑lived

***

## Problems with `synchronized`

✅ Simple and safe  
❌ No try‑lock  
❌ No timeout  
❌ Entire block/method locked

This is why explicit locks exist.

***

## Explicit Locks – `java.util.concurrent.locks.Lock`

```java
Lock lock = new ReentrantLock();

lock.lock();
try {
    count++;
} finally {
    lock.unlock();
}
```

✅ More flexible  
✅ Explicit lock control  
✅ Supports advanced features

***

## Why `finally` Block Is Mandatory

If you forget to unlock:
❌ Deadlock risk

Rule:

> **Always release locks in a `finally` block.**

This is a classic interview check.

***

## `ReentrantLock` Key Features

✅ Reentrant (same thread can re‑acquire)  
✅ Try‑lock  
✅ Timeout support  
✅ Fairness option

```java
lock.tryLock();
```

Allows:
✅ Avoiding indefinite blocking

***

## Fair vs Unfair Locks

*   **Fair lock**: threads acquire lock in order
*   **Unfair lock**: faster, no ordering guarantee

Trade‑off:
✅ Fair → predictability  
✅ Unfair → performance

Most systems prefer **unfair locks**.

***

## `synchronized` vs `ReentrantLock` (Interview Table)

| Aspect      | synchronized | ReentrantLock |
| ----------- | ------------ | ------------- |
| Simplicity  | ✅            | ❌             |
| Flexibility | ❌            | ✅             |
| Try‑lock    | ❌            | ✅             |
| Fairness    | ❌            | ✅             |
| Error‑prone | ✅ low        | ❌ higher      |

Interview insight:

> Use `synchronized` by default, `Lock` when you need flexibility.

***

## Deadlocks (Critical Topic)

> **Deadlock occurs when two or more threads wait forever for each other’s locks.**

Classic pattern:

*   Thread A holds Lock 1, waits for Lock 2
*   Thread B holds Lock 2, waits for Lock 1

***

## How to Prevent Deadlocks

✅ Acquire locks in consistent order  
✅ Avoid nested locks when possible  
✅ Use timeouts (`tryLock`)  
✅ Minimize lock scope

Deadlocks are **design bugs**, not JVM bugs.

***

## Synchronization Best Practices

✅ Keep critical sections small  
✅ Protect only shared mutable data  
✅ Avoid long‑running operations inside locks  
✅ Prefer higher‑level concurrency utilities

***

## Interview‑Ready Explanation (Use This)

> “Synchronization protects shared mutable state by ensuring mutual exclusion and memory visibility. Java provides intrinsic locks using `synchronized` and explicit locks like `ReentrantLock`. `synchronized` is simpler and safer, while explicit locks offer more flexibility but require careful handling to avoid deadlocks.”

Clear. Calm. Senior‑level ✅

***

## Key Takeaways

✅ Synchronization prevents race conditions  
✅ `synchronized` uses intrinsic locks  
✅ Explicit locks provide flexibility  
✅ Poor locking causes contention and deadlocks  
✅ Correctness comes before performance

> **Concurrency bugs are caused by unprotected shared state.**

