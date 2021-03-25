package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.techtown.cryptoculus.repository.CoinRepository
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.DataParser
import org.techtown.cryptoculus.repository.network.RetrofitClient

class ViewModel(application: Application) : ViewModel(){
    // !restartApp -> insert(), restartApp -> update()

    val publishSubject: PublishSubject<String> = PublishSubject.create()

    private val disposable: CompositeDisposable = CompositeDisposable()
    private val coinRepository: CoinRepository by lazy {
        CoinRepository(application)
    }


    var restartApp = false
    var exchange = "coinone"
    /*
        disposable.add(coinRepository.insert(coinInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
     */
    private var coinInfos = MutableLiveData<ArrayList<CoinInfo>>()

    private var coinInfosFromDB = MutableLiveData<ArrayList<CoinInfo>>()

    init {
        publishSubject.subscribe { exchange ->
            coinInfos.value = coinRepository.getCoinInfos(exchange)
            // DB에서 받을 때 말곤 swipeRefreshLayout이랑 menu에서만 사용한다
        }
    }
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            return ViewModel(application) as T
        }
    }

    fun onCreate() {
        coinInfos.value = coinRepository.getCoinInfos("coinone")
        // exchange 저장하는 거 만들면 바꿔준다. 일단은 직접 지정.
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    @JvmName("getCoinInfos1") // 구조 상 존재하는 'coinInfos'의 getter와 충돌 방지
    fun getCoinInfos() = coinInfos

    @JvmName("getCoinInfosFromDB1")
    fun getCoinInfosFromDB() = coinInfosFromDB
    // 얘는 관찰할 필요가 없지 않아?
    // 어차피 첫 실행 때 새로 받아온 데이터랑 비교 한 번 하면 끝나는 건데

    fun insert(coinInfo: CoinInfo, next: () -> Unit) {
        disposable.add(coinRepository.insert(coinInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
    }

    fun update(coinInfo: CoinInfo, next: () -> Unit) {
        disposable.add(coinRepository.update(coinInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
    }

    fun insertAll(coinInfos: ArrayList<CoinInfo>, next: () -> Unit) {
        disposable.add(coinRepository.insertAll(coinInfos as List<CoinInfo>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
    }

    fun updateAll(coinInfos: ArrayList<CoinInfo>, next: () -> Unit) {
        disposable.add(coinRepository.updateAll(coinInfos as List<CoinInfo>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
    }

    fun println(data: String) {
        Log.d("ViewModel", data)
    }

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
}