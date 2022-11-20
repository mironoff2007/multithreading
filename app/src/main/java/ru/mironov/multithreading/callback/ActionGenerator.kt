package ru.mironov.multithreading.callback

class ActionGenerator {

    var listener: OnActionListener? = null

    fun generateActionAfter(delay: Long) {
        val thread = object : Thread() {
            override fun run() {
                sleep(delay)
                val action = OnActionListener.Action("some action")
                listener?.onAction(action)
                super.run()
            }
        }
        thread.start()
    }
}