package ru.mironov.multithreading

import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ThreadsTest {

    @Test
    fun deadlockTest() {
        val lock1 = Any()
        val lock2 = Any()
        val executor = Executors.newFixedThreadPool(2)

        val result = AtomicBoolean(true)

        executor.execute {
            synchronized(lock1) {
                Thread.sleep(1000)
                synchronized(lock2) {
                    Thread.sleep(1000)
                    result.set(false)
                    println ("thread1")
                }
            }
        }

        executor.execute {
            synchronized(lock2) {
                Thread.sleep(1000)
                synchronized(lock1) {
                    Thread.sleep(1000)
                    result.set(false)
                    println("thread2")
                }
            }
        }
        println("end")
        assert(result.get())
    }
}