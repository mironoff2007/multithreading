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

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testCoroutines1() = runBlocking {
        withContext(Dispatchers.Main) {
            Assert.assertEquals(job1().await() and job2().await(), false)
        }
    }

    @Test
    fun testCoroutines2() = runBlocking {
        withContext(Dispatchers.Main) {
            Assert.assertEquals(job1().await() or job2().await(), true)
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