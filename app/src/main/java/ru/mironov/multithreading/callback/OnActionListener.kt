package ru.mironov.multithreading.callback

interface OnActionListener {

    class Action(val msg: String)

    fun onAction(action: Action)
}

