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

    /*
        disposable.add(coinRepository.insert(coinInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { next() })
     */
    /*
        disposable.add(coinRepository.getCoinInfos(exchange)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { next() })
     */
    var liveCoinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val coinInfos: ArrayList<CoinInfo> by lazy {
        coinRepository.exchange = this.exchange
        coinRepository.getCoinInfos()
        // 이렇게 해 놓으면 액티비티에서 viewModel.coinInfos가 호출될 때마다
        // 자동으로 업데이트 될 수 있다
        // 이걸 네트워크로 돌리고
        // DB로 받아오는 건 따로 만들자
        // DB 사용을 비교할 때 말고는 하지 않을 것 같다
        // 아니다
        // 오히려 비교할 때 말고 액티비티에 넣을 일은 없지 않아?
        // 항상 새 걸 넣어줘야 하잖아
    }

    private val coinInfosFromDB: ArrayList<CoinInfo> by lazy {
        coinRepository.exchange = this.exchange
        coinRepository.getCoinInfosFromDB()
    }

    var restartApp = false
    var exchange = "coinone"

    init {
        publishSubject.subscribe { exchange ->
            this.exchange = exchange
            // 여기서 coinInfos 새로 초기화하면 되는 거 아냐?
            // exchange는 새로 들어오잖아
            // DB에서 받을 때 말곤 swipeRefreshListener랑 menu 말곤 새로 받을 일이 없다
        }
    }

    // val coinInfos = ArrayList<CoinInfo>()

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            return ViewModel(application) as T
        }
    }

    fun onCreate() {

    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    @JvmName("getCoinInfos1") // 구조 상 존재하는 'coinInfos'의 getter와 충돌 방지
    fun getCoinInfos() = coinInfos

    @JvmName("getCoinInfosFromDB1")
    fun getCoinInfosFromDB() = coinInfosFromDB

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