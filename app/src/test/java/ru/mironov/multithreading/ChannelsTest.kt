package ru.mironov.multithreading

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class ChannelsTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        //Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanUp() {
        //Dispatchers.resetMain()
        //testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testChannels() = runBlocking {
        println("start")
        val channels = Channels()
        launch { channels.produce() }
        println("is empty ${channels.getChannel().isEmpty}")
        while (!channels.getChannel().isClosedForReceive) {
            val v = channels.getChannel().receive()
            println("receive ${v}")
            println("is empty before delay ${channels.getChannel().isEmpty}")
            delay(700)
            println("is empty after delay ${channels.getChannel().isEmpty}")
        }
    }

}