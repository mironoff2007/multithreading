package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import ru.mironov.multithreading.FlowExamples
import ru.mironov.multithreading.ProducerConsumer

class CustomBlockingQueueTest {

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
    fun test1() = runBlocking {
        val queue = ProducerConsumer.CustomBlockingQueue<Int>()
        val count = 30
        queue.dataAddedCallback = {}
        launch(IO) {
            repeat(count / 2) {
                delay(500)
                queue.post(1)
            }
            println("done 1")
        }
        launch(IO) {
            repeat(count / 2) {
                delay(300)
                queue.post(2)
            }
            println("done 2")
        }
        val list = mutableListOf<Int>()
        repeat(count) {
            val v = queue.getWithWait()
            println("value $v, index $it")
            list.add(it)
        }
        Assert.assertEquals(count, list.size)
    }

}