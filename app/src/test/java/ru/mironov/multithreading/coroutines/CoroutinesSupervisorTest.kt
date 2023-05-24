package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Before
import java.util.concurrent.atomic.AtomicBoolean

class CoroutinesSupervisorTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    private val supervisor = SupervisorJob()
    private val testScope = CoroutineScope(testDispatcher)

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
    fun testNoSupervisor() = runBlocking {
        val secondJobDone = AtomicBoolean(false)
        val thirdJobDone = AtomicBoolean(false)
        val parentJobDone = AtomicBoolean(false)
        val scope = CoroutineScope(IO)
        scope.launch() {
            scope.launch() {
                delay(50)
                throw Exception("Test exception")
            }
            scope.launch() {
                secondJobDone.set(true)
                println("thread without exception")
            }
            scope.launch() {
                delay(200)
                thirdJobDone.set(true)
                println("thread without exception")
            }
            delay(300)
            parentJobDone.set(true)
        }
        delay(500)
        assert(secondJobDone.get()) { "second job is done" }
        assert(!thirdJobDone.get()) { "third job is failed" }
        assert(!parentJobDone.get()) { "parent job is failed" }
    }


    @Test
    fun testSupervisor() = runBlocking {
        val secondJobDone = AtomicBoolean(false)
        val thirdJobDone = AtomicBoolean(false)
        val parentJobDone = AtomicBoolean(false)
        val scope = CoroutineScope(IO)
        scope.launch(supervisor) {
            scope.launch() {
                delay(50)
                throw Exception("Test exception")
            }
            scope.launch() {
                secondJobDone.set(true)
                println("thread without exception")
            }
            scope.launch() {
                delay(200)
                thirdJobDone.set(true)
                println("thread without exception")
            }
            delay(300)
            parentJobDone.set(true)
        }
        delay(500)
        assert(secondJobDone.get()) { "second job is done" }
        assert(!thirdJobDone.get()) { "third job is done" }
        assert(parentJobDone.get()) { "parent job is done" }
    }

}