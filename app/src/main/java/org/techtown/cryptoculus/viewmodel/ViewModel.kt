package org.techtown.cryptoculus.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.subjects.PublishSubject
import org.techtown.cryptoculus.repository.model.CoinDao
import org.techtown.cryptoculus.repository.model.CoinDatabase
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.Client

class ViewModel(application: Application) : ViewModel(){
    private val client = Client()
    var coinInfos = ArrayList<CoinInfo>()
    val publishSubject: PublishSubject<String> = PublishSubject.create()
    val coinDao: CoinDao = CoinDatabase.getInstance(application)!!.coinDao()

    init {
        publishSubject.subscribe { exchange ->
            liveCoinInfos.value = client.getData(exchange)

            // 연동을 해버릴까?
            // 다른 rx랑 말이야
            // 여기서 exchange를 그 rx에 onNext로 주고 나서
            // 통지 들어오면 바꾸는 식으로
        }.dispose()
    }

    val observable = Observable.create<String> {
        it.onNext("")
    }
    val observer = object: DisposableObserver<String>() {
        override fun onNext(t: String) {

        }

        override fun onComplete() {

        }

        override fun onError(e: Throwable) {

        }
    }
    // 이 경우에 통지하는 건 MainActivity지
    // 옵저버가 여기서 받아야 하는 거고
    // onNext가 실행되면 observer는 그걸 받아서 가공하는 거지
    // ???

    // val coinInfos = ArrayList<CoinInfo>()
    val liveCoinInfos = MutableLiveData<ArrayList<CoinInfo>>() // MainActivity에서 observe 후 변경되면 어댑터 재설정


    /*
    val source = Observable.create<String> { it->
        it.onNext("Hello RxAndroid World")
        it.onComplete()
    }
    source.subscribe(observer)

    var observer = object:DisposableObserver<String>() {
        override fun onComplete() {
            Log.d("TEST","observer onComplete")
        }
        override fun onNext(t: String) {
            main_text.text = t
        }
        override fun onError(e: Throwable) {
        }
    }
    // Observable에서 onNext가 실행될 때마다, 즉 데이터가 통지될 때마다
    // 텍스트를 통지된 데이터로 바꾼다

    */

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            return ViewModel(application) as T
        }
    }

    fun onCreate() {
        if (coinDao.getAll().isNotEmpty()) {

        }
        else
            coinInfos = client.getData("coinone")
    }
}