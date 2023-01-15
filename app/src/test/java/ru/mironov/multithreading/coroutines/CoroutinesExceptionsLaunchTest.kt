package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Before

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
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testCoroutines1() {
        scope.launch (testDispatcher) {
            var flag = false
            try {
                throw Exception()
            }
            catch (e:Exception)
            {
                flag = true
            }
            assert(flag)
        }
    }

    @Test
    fun testChildCoroutines() {
        var flag = false
        scope.launch(testDispatcher) {
            scope.launch(testDispatcher) {
                try {
                    throw Exception()
                } catch (e: Exception) {
                    flag = true
                }
            }
        }
        assert(flag)
    }

    @Test
    fun testChildExceptionMissed() {
        var flag = false
        try {
            scope.launch(testDispatcher) {
                try {
                    scope.launch(testDispatcher) { throw Exception("Test exception") }
                } catch (e: Exception) {
                    flag = true
                }
            }
        } catch (e: Exception) {
            flag = true
        }
        assert(!flag)
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
    fun testChildExceptionCatchByScope() {
        var flag = false
        scope.launch(testDispatcher) {
            try {
                coroutineScope { launch(testDispatcher) { throw Exception() } }
            } catch (e: Exception) {
                flag = true
            }
        }
        assert(flag)
    }
}