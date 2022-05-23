package ru.mironov.multithreading

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
}