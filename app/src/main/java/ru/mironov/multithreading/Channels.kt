package ru.mironov.multithreading

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

class Channels {

    val channel = Channel<Int>()

    suspend fun produce() {
        for (i in 0..10) {
            delay(100)
            channel.send(i)
            println("send $i")
        }
        channel.close()
    }
}