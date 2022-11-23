package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import ru.mironov.multithreading.FlowExamples

class CoroutinesUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    private val supervisor = SupervisorJob()
    val scope = CoroutineScope(testDispatcher + supervisor)

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
    fun testCoroutines1() = runBlocking {
        withContext(Dispatchers.Main) {
            Assert.assertEquals(false, false)
        }
    }

    @Test
    fun testCoroutines2() = runBlocking {
        withContext(Dispatchers.Main) {
            FlowExamples.flowExample1()
            Assert.assertEquals(true, true)
        }
    }

    @Test
    fun testCoroutines3() = runBlocking {
        launch {
            delay(200L)
            println("Hello 1")
        }
        coroutineScope {
            launch {
                delay(500L)
                println("Hello 2")
            }
            delay(100L)
            println("Hello 3")
        }
        println("Hello 4")
        //3124
    }

    @Test
    fun testCoroutines5() = runBlocking {
        launch {
            delay(200L)
            println("Hello 1")
        }
        coroutineScope {
            launch {
                delay(50L)
                println("Hello 2")
            }
            delay(100L)
            println("Hello 3")
        }
        println("Hello 4")
        //2341
    }

    @Test
    fun testAsync() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job1 = job1()
        val job2 = job2()
        val job3 = job3()

        val res1 = job1.await()
        val res2 = job2.await()
        val res3 = job3.await()

        println(System.currentTimeMillis() - startTime)
        assert(res1 && res2 && res3)
    }

    suspend fun job1() = scope.async(Dispatchers.Default) {
        delay(1000)
        println("job1 done")
        true
    }

    suspend fun job2() = scope.async(Dispatchers.Default) {
        delay(500)
        println("job2 done")
        true
    }

    suspend fun job3() = scope.async(Dispatchers.Default) {
        delay(1500)
        println("job3 done")
        true
    }

}