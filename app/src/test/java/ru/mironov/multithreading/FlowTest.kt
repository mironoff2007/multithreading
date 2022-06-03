package ru.mironov.multithreading

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class FlowTest {

    @Before
    fun before() {}

    @After
    fun after() {}

    @Test
    fun testSharedFlow() = runBlocking {
        println("testSharedFlow")
        val flow = MutableSharedFlow<Int>()

        var res: Int? = null

        launch {
            res = flow.first()
        }

        delay(100)

        flow.emit(1)

        assert(res == 1)
    }

    @Test
    fun testStateFlow() = runBlocking{
        val flow = MutableStateFlow<Int>(0)

        flow.emit(1)

        assert(flow.first() == 1)
    }

    @Test
    fun testStateFlowCollect() = runBlocking{
        val flow = MutableStateFlow<Int>(0)

        delay(100)
        val list = mutableListOf<Int>()

        val job = launch {
            flow.collect{
                println("-- $it")
                list.add(it)
            }
        }

        flow.emit(1)
        delay(100)

        flow.emit(2)
        delay(100)

        job.cancel() //collect never ends
        assert(list.size == 2)
    }

    @Test
    fun testColdFlowCollect() = runBlocking{

        val list = mutableListOf<Int>()

        val flow = flow {
            emit(1)
            delay(100)
            emit(2)
        }

        flow.collect {
            println(it)
            list.add(it)
        }

        assert(true)
    }
}