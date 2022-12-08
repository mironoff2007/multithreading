package ru.mironov.multithreading

import kotlinx.coroutines.*

class WaitJob(dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(dispatcher + supervisor)

    private val job: Job

    init {
        job = scope.launch {
            println("start job")
            delay(2000)
            println("end job")
        }
    }

    suspend fun doOrWait(){
        println("doOrWait")
        job.join()
        println("done")
    }
}