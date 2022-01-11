package ru.mironov.multithreading

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object FlowExamples {

    suspend fun flowExample1() {
        coroutineScope {
            launch {

                flow {
                    emit(1)
                    emit(2)
                    emit(3)
                }
                    .map {
                        println("map1 value = $it")
                        it + it
                    }
                    .onEach { println("after map1 -> $it") }
                    .flowOn(Dispatchers.IO)
                    .map {
                        println(" map2 value = $it")
                        it * it
                    }
                    .onEach { println("after map2 -> $it") }
                    .flowOn(Dispatchers.Default)
                    .onCompletion { println("onCompletion") }
                    .collect {
                        println("received value $it")
                    }
            }
        }
    }
}