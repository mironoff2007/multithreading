package ru.mironov.multithreading

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay

class Channels {

    private val channel = Channel<Int>()

    fun getChannel(): ReceiveChannel<Int>{
        return channel
    }

    suspend fun produce() {
        for (i in 0..10) {
            delay(100)
            channel.send(i)
            println("send $i")
        }
        channel.close()
    }

}