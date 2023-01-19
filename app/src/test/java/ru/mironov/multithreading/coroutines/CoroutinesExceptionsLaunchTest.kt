package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Before
import java.util.concurrent.atomic.AtomicBoolean

class CoroutinesExceptionsLaunchTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(testDispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanUp() {
        //Dispatchers.resetMain()
        //testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun testChildExceptionCatchByJoin() {
        var flag = false
        scope.launch(testDispatcher) {
            try {
                val job = scope.launch(testDispatcher) { throw Exception() }
                job.join()
            } catch (e: Exception) {
                flag = true
            }
        }
        assert(flag)
    }

    @Test
    fun testContextSwitchExceptionCatch() {
        var caught = false
        scope.launch(testDispatcher) {
            try {
                withContext(testDispatcher) { throw Exception("Test exception") }
            } catch (e: Exception) {
                caught = true
            }
        }
        assert(caught)
    }

    @Test
    fun testExceptionMissed() {
        var flag = false
        try {
            scope.launch(testDispatcher) {
                throw Exception("Test exception")
            }
        } catch (e: Exception) {
            flag = true
        }
        assert(!flag)
    }

    @Test
    fun testSupervisor() {
        val caught = AtomicBoolean(false)
        scope.launch() {
            scope.launch(supervisor) {
                throw Exception("Test exception")
            }
            scope.launch(supervisor) {
                caught.set(true)
                println("thread without exception")
            }
            delay(100)
        }
        assert(caught.get())
    }

    @Test
    fun testWithoutSupervisor() {
        val notCaught = AtomicBoolean(true)
        scope.launch() {
            scope.launch() {
                throw Exception("Test exception")
            }
            scope.launch() {
                notCaught.set(false)
                println("thread without exception")
            }
            delay(100)
        }
        assert(notCaught.get())
    }

    @Test
    fun testExceptionHandler() {
        val caught = AtomicBoolean(false)
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
            caught.set(true)
        }
        scope.launch() {
            scope.launch(handler) {
                throw Exception("Test exception")
            }
        }
        assert(caught.get())
    }


    @Test
    fun testChildExceptionCatchByScope() {
        var caught = false
        scope.launch(testDispatcher) {
            try {
                coroutineScope { launch(testDispatcher) { throw Exception() } }
            } catch (e: Exception) {
                caught = true
            }
        }
        assert(caught)
    }

    @Test
    fun testChildExceptionMissedByScopeSupervisor() {
        var caught = false
        scope.launch(testDispatcher + supervisor) {
            try {
                coroutineScope { launch(testDispatcher + supervisor) { throw Exception() } }
            } catch (e: Exception) {
                caught = true
            }
        }
        assert(!caught)
    }

}