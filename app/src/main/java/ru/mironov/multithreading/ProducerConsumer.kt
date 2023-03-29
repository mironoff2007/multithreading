package ru.mironov.multithreading

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

object ProducerConsumer {

    //должен получать данные +
    //должен отдавать данные +
    //получать данные с ожиданием +
    //буфер +
    //потокобезопасный +
    //уведомления о новых данных+

    class CustomBlockingQueue<T>() {
        private val mutex = Mutex()
        private val getValueLock = Mutex(locked = true)
        private val buffer = mutableListOf<T>()

        var dataAddedCallback:(() -> Unit)? = null

        suspend fun post(value : T) {
            mutex.withLock {
                buffer.add(value)
                dataAddedCallback?.invoke()
                try {
                    getValueLock.unlock()
                }
                catch (e: Exception) {

                }

            }
        }

        suspend fun get(): T? {
            return mutex.withLock {
                getValueLock.lock()
                buffer.lastOrNull()
            }
        }

        suspend fun getWithWait(): T? {
            getValueLock.withLock {}
            return get()
        }

    }
}