
# Common Concurrency Bugs – What Goes Wrong and Why

Most concurrency bugs:
❌ Do not appear in development  
❌ Cannot be reproduced reliably  
❌ Show up only under load  

That makes them **some of the most expensive bugs in production**.

This phase focuses on the **most common concurrency failure patterns**, why they occur, and how to avoid them.

---

## Why Concurrency Bugs Are Dangerous

Concurrency bugs are:
- Non‑deterministic
- Timing‑dependent
- Environment‑dependent

A bug may:
✅ Appear once in production  
❌ Never appear again  

Understanding bug *patterns* is the only reliable defense.

---

## 1️⃣ Race Condition

> **A race condition occurs when multiple threads access shared data and the result depends on execution timing.**

---

### Example (Conceptual)

Two threads incrementing a counter:

```java
count++;
````

Steps:

1.  Read `count`
2.  Increment
3.  Write back

If two threads interleave:
❌ One update is lost

***

### Why It Happens

*   Read–modify–write is not atomic
*   No synchronization
*   Shared mutable state

***

### How to Prevent

✅ Synchronization (`synchronized`)  
✅ Locks (`ReentrantLock`)  
✅ Atomic classes (`AtomicInteger`)

***

### Interview Insight

> **Race conditions are caused by unprotected shared mutable state.**

***

## 2️⃣ Visibility Bug (Stale Reads)

> **A thread sees outdated values written by another thread.**

This happens even **without race conditions**.

***

### Example

```java
boolean running = true;

while (running) {
    // loop forever
}
```

Another thread sets `running = false`,
but the loop never exits.

***

### Why It Happens

*   CPU caching
*   JVM reordering
*   No happens‑before relationship

***

### Fix

✅ Use `volatile`  
✅ Use synchronization  
✅ Use proper locking

***

## 3️⃣ Deadlock (Critical Interview Topic)

> **A deadlock occurs when two or more threads wait forever for each other’s locks.**

***

### Classic Deadlock Pattern

*   Thread A holds Lock 1 → waiting for Lock 2
*   Thread B holds Lock 2 → waiting for Lock 1

Both wait forever.

***

### Why It Happens

*   Multiple locks
*   Inconsistent lock acquisition order

***

### How to Prevent Deadlocks

✅ Always acquire locks in the same order  
✅ Minimize nested locks  
✅ Use `tryLock` with timeout  
✅ Reduce lock scope

***

### Interview Insight

> **Deadlocks are design bugs, not JVM bugs.**

***

## 4️⃣ Livelock

> **Threads are not blocked, but they keep reacting to each other and make no progress.**

***

### Difference from Deadlock

| Deadlock        | Livelock        |
| --------------- | --------------- |
| Threads blocked | Threads running |
| No progress     | No progress     |
| Waiting         | Reacting        |

***

### Example (Conceptual)

Two threads:

*   Both retry when conflict detected
*   Both back off simultaneously
*   Both retry again

Forever.

***

### How to Fix

✅ Add randomness (backoff strategy)  
✅ Limit retries  
✅ Revisit coordination logic

***

## 5️⃣ Thread Starvation

> **A thread never gets CPU or lock time to make progress.**

***

### Causes

*   Unfair locks
*   CPU‑bound tasks blocking I/O tasks
*   Thread pool starvation
*   Long‑running tasks inside synchronized blocks

***

### Example

*   Thread pool size = 10
*   All threads blocked waiting for DB
*   New tasks never execute

***

### Prevention

✅ Proper thread pool sizing  
✅ Separate pools for different workloads  
✅ Avoid blocking operations

***

## 6️⃣ Blocking in Thread Pools (Very Common)

> **Blocking inside thread pool tasks causes system‑wide stalls.**

***

### Example

*   Web request thread waits on:
    *   Future.get()
    *   Synchronous I/O
    *   External service

Under load:
❌ Thread pool exhausts  
❌ Requests queue up  
❌ System appears “hung”

***

### Fix

✅ Use async I/O  
✅ Use separate pools  
✅ Avoid blocking calls

***

## 7️⃣ Incorrect Use of `volatile`

> `volatile` is often **misused**.

***

### Common Mistake

```java
volatile int count;
count++; // NOT thread‑safe
```

Why broken?
❌ `volatile` does not provide atomicity

***

### Correct Usage

✅ Flags  
✅ State checks  
❌ Counters  
❌ Collections

***

## 8️⃣ Double‑Checked Locking (Famous Trap)

> **Incorrect double‑checked locking leads to partially constructed objects.**

***

### Historical Bug

```java
if (instance == null) {
    synchronized (this) {
        if (instance == null) {
            instance = new Singleton();
        }
    }
}
```

Without `volatile`, this is broken due to reordering.

***

### Fix

✅ Use `volatile`  
✅ Or simpler designs (enums, static holders)

***

## 9️⃣ Forgetting Happens‑Before

> **Most subtle bugs exist because there is no happens‑before relationship.**

If:

*   No `volatile`
*   No lock
*   No thread start/join
*   No concurrency utility

Then:
❌ Visibility is undefined

Always ask:

> “What creates the happens‑before relationship here?”

***

## 🔟 Over‑Synchronization

> **Too much synchronization hurts performance and scalability.**

Symptoms:

*   Lock contention
*   Low throughput
*   High latency under load

Balance is critical:
✅ Synchronize shared mutable state  
❌ Everything else

***

## Common Interview Red Flags

❌ “volatile makes code thread‑safe”  
❌ “Deadlocks don’t happen often”  
❌ “Thread scheduling is predictable”  
❌ “Thread pools solve concurrency issues”

***

## Interview‑Ready Explanation (Use This)

> “Most concurrency bugs fall into patterns like race conditions, visibility issues, deadlocks, livelocks, and thread starvation. They arise from unprotected shared state, missing happens‑before relationships, or poor locking and thread‑pool design. Preventing them requires disciplined synchronization and awareness of execution behavior.”

Clear. Confident. Senior‑level ✅

***

## Key Takeaways

✅ Concurrency bugs are pattern‑based  
✅ Race conditions and visibility bugs are most common  
✅ Deadlocks are design issues  
✅ Thread pools don’t fix data safety  
✅ Happens‑before reasoning explains behavior

> **Concurrency bugs come from what you didn’t protect or didn’t guarantee.**

***

## Repository Completion

You have now completed:
✅ Concurrency fundamentals  
✅ Thread lifecycle  
✅ Synchronization & locks  
✅ Memory visibility (`volatile`)  
✅ Happens‑before reasoning  
✅ Executors & thread pools  
✅ Concurrency failure patterns

***

✅ **End of Java Concurrency & Multithreading Repository**

