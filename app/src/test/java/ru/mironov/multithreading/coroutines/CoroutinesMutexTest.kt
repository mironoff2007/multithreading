package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import ru.mironov.multithreading.FlowExamples
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutinesMutexTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    private val supervisor = SupervisorJob()
    val scope = CoroutineScope(testDispatcher + supervisor)

    val mutex = Mutex()

    val handler = CoroutineExceptionHandler { _, exception ->
        println(exception)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testMutex() = runBlocking {
        withContext(testDispatcher + handler) {
            mutex.withReentrantLock {
                println("outer lock")
                mutex.withReentrantLock {
                    println("inner lock1")
                }
                mutex.withReentrantLock {
                    println("inner lock2")
                }
            }
            Assert.assertEquals(false, false)
        }
    }

    suspend fun <T> Mutex.withReentrantLock(block: suspend () -> T): T {
        val key = ReentrantMutexContextKey(this)
        // call block directly when this mutex is already locked in the context
        if (coroutineContext[key] != null) return block()
        // otherwise add it to the context and lock the mutex
        return withContext(ReentrantMutexContextElement(key)) {
            withLock { block() }
        }
    }

    class ReentrantMutexContextElement(
        override val key: ReentrantMutexContextKey
    ) : CoroutineContext.Element

    data class ReentrantMutexContextKey(
        val mutex: Mutex
    ) : CoroutineContext.Key<ReentrantMutexContextElement>

}