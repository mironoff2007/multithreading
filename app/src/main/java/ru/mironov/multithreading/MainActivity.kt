package ru.mironov.multithreading

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var dispose: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dispose = dataSource()
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                Log.d("My_tag", "next int $it")
            }, {

            }, {

            })
    }

    private fun dataSource(): Observable<Int> {
        return Observable.create { subscriber ->
            for (i in 0..100) {
                subscriber.onNext(i)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.dispose()
    }
}