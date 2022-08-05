package ru.mironov.multithreading.rxjava

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RXJavaTest {

    @Before
    fun before() {
    }

    @After
    fun after() {
    }

    @Test
    fun zipTest() {
        var complete = false
        val alphabets1 =
            Observable.intervalRange(0, 2, 1, 100, TimeUnit.MILLISECONDS)
                .map { id -> "A" + id }
        val alphabets2 =
            Observable.intervalRange(0, 5, 2, 150, TimeUnit.MILLISECONDS)
                .map { id -> "B" + id }

        Observable.zip(alphabets1, alphabets2,
            BiFunction<String, String, String> { t1, t2 -> "$t1 $t2" })
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                    complete = true
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: String) {
                    println("onNext: $t")
                }

                override fun onError(e: Throwable) {

                }
            })

        while (!complete){
            Thread.sleep(100)
        }
        assert(true)
    }

}