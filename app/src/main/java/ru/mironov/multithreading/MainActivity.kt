package ru.mironov.multithreading

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    //test push from chipmunk
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dispose = dataSource()
            .subscribeOn(Schedulers.newThread())
            .subscribe({
            Log.d("My_tag", "next int $it")
            },{

            },{

            })
    }

    fun dataSource(): Observable<Int> {
        return Observable.create{ subscriber->
            for (i in 0..100){
                subscriber.onNext(i)
            }
        }
    }
}