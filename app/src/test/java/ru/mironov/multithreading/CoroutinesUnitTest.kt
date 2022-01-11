package ru.mironov.multithreading

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class CoroutinesUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

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

    suspend fun job1() = GlobalScope.async(Dispatchers.Default) {
        delay(1000)
        true
    }

    suspend fun job2() = GlobalScope.async(Dispatchers.Default) {
        delay(500)
        false
    }
}