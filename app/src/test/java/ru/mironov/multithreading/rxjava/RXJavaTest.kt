package ru.mironov.multithreading.rxjava

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.util.concurrent.TimeUnit

class RXJavaTest {

    @get:Rule
    var name: TestName = TestName()

    @Before
    fun before() {
        println("---")
    }

    @After
    fun after() {
    }

    @Test
    fun zipTest() {
        println(name.methodName)
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


    @Test
    fun testFlatMap(){
        //not guarantees order
        println(name.methodName)
        fun getModifiedObservable(integer: Int): Observable<Int> {
            return Observable.create(ObservableOnSubscribe<Int> { emitter ->
                emitter.onNext(integer * 2)
                emitter.onComplete()
            })
                .subscribeOn(Schedulers.io())
        }

        Observable.just(1,2,3,4,5,6)
            .flatMap { t -> getModifiedObservable(t) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe {
                println("onNext: $it")
            }

        Thread.sleep(1000)
    }

    @Test
    fun testConcatMap(){
        //guarantees order
        val list = mutableListOf<Int>()
        println(name.methodName)
        fun getModifiedObservable(integer: Int): Observable<Int> {
            return Observable.create(ObservableOnSubscribe<Int> { emitter ->
                emitter.onNext(integer * 2)
                emitter.onComplete()
            })
                .subscribeOn(Schedulers.io())
        }

        Observable.fromArray(1,2,3,4)
            .concatMap { t -> getModifiedObservable(t) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe {
                println("onNext: $it")
                list.add(it)
            }

        Thread.sleep(1000)
        var i = 1
        list.forEach {
            assert(it == i * 2)
            i++
        }
    }

    @Test
    fun schedulersTest1() {
        println(name.methodName)
        var thread1Name = ""
        var thread2Name = ""
        var thread3Name = ""
        Single.just("Some string")
            .map { str ->
                str.length.also {
                    println("map 1 thread " + Thread.currentThread().name)
                    thread1Name = Thread.currentThread().name
                }
            }
            .subscribeOn(Schedulers.io())
            .map { length ->
                42 * length.also {
                    println("map2 thread " + Thread.currentThread().name)
                    thread2Name = Thread.currentThread().name
                }
            }
            .observeOn(Schedulers.computation())
            .subscribe { number ->
                println("Sample number = $number")
                println("subscribe thread " + Thread.currentThread().name)
                thread3Name = Thread.currentThread().name
            }
        Thread.sleep(1000)

        assert(thread1Name.contains(IO_THREADS_NAME))
        assert(thread2Name.contains(IO_THREADS_NAME))
        assert(thread3Name.contains(COMPUTATION_THREADS_NAME))
    }

    @Test
    fun schedulersTest2() {
        println(name.methodName)
        var thread1Name = ""
        var thread2Name = ""
        var thread3Name = ""
        Single.just("Some string")
            .map { str ->
                str.length.also {
                    println("map 1 thread " + Thread.currentThread().name)
                    thread1Name = Thread.currentThread().name
                }
            }
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.io())
            .map { length ->
                42 * length.also {
                    println("map2 thread " + Thread.currentThread().name)
                    thread2Name = Thread.currentThread().name
                }
            }
            .subscribe { number ->
                println("Sample number = $number")
                println("subscribe thread " + Thread.currentThread().name)
                thread3Name = Thread.currentThread().name
            }
        Thread.sleep(1000)

        assert(thread1Name.contains(IO_THREADS_NAME))
        assert(thread2Name.contains(COMPUTATION_THREADS_NAME))
        assert(thread3Name.contains(COMPUTATION_THREADS_NAME))
    }

    companion object{
        const val IO_THREADS_NAME = "RxCachedThreadScheduler"
        const val COMPUTATION_THREADS_NAME = "RxComputationThreadPool"
    }
}