package ru.mironov.multithreading.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Test
import org.junit.Before
import ru.mironov.multithreading.callback.ActionGenerator
import ru.mironov.multithreading.callback.OnActionListener

class CallbackTest {

    private var action: OnActionListener.Action? = null

    private val actionGenerator = ActionGenerator()

    private val listener = object : OnActionListener {
        override fun onAction(retunedAction: OnActionListener.Action) {
            action = retunedAction
            println("callback action")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun suspendListener(actionGenerator: ActionGenerator, delay: Long): OnActionListener.Action = suspendCancellableCoroutine { cont ->
        val callback = object : OnActionListener {
            override fun onAction(action: OnActionListener.Action) {
                cont.resume(action, {})
            }
        }
        actionGenerator.listener = callback
        actionGenerator.generateActionAfter(delay)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatcher = TestCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        //Dispatchers.setMain(testDispatcher)
        action = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanUp() {
        //Dispatchers.resetMain()
        //testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testCallback() = runBlocking {
        println("start generator")
        actionGenerator.listener = listener
        val delay = 1000L
        actionGenerator.generateActionAfter(delay)
        delay(delay + 100)
        println("listener ${action?.msg}")
        assert(action != null)
    }

    @Test
    fun testCallbackSuspend() = runBlocking {
        println("start generator")
        val action = suspendListener(actionGenerator, 1000)
        println("suspend listener ${action.msg}")
    }

}