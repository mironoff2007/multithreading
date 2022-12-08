package ru.mironov.multithreading

import kotlinx.coroutines.*

class WaitJob(dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(dispatcher + supervisor)

    private val job: Job = scope.launch(start = CoroutineStart.LAZY) {
        println("start job")
        delay(2000)
        println("end job")
    }

    fun startJob(){
        job.start()
    }

    suspend fun doOrWait() {
        println("doOrWait")
        if (job.isActive) println("waiting")
        job.join()
        println("done")
    }
}