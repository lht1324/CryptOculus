package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.subjects.PublishSubject
import org.techtown.cryptoculus.repository.CoinRepository
import org.techtown.cryptoculus.repository.model.CoinInfo

class ViewModel(application: Application) : ViewModel(){
    // !restartApp -> insert(), restartApp -> update()

    val publishSubject: PublishSubject<String> = PublishSubject.create()
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

    @JvmName("getCoinInfos1") // 구조 상 존재하는 'coinInfos'의 getter와 충돌 방지
    fun getCoinInfos() = coinInfos

    init {
        coinRepository.onCreate()
        publishSubject.subscribe { exchange ->
            this.exchange = exchange
            coinRepository.getData(exchange)
            // 이걸 실행하면 클라이언트의 라이브데이터가 바뀌면서 뷰모델까지 관찰 결과가 올라와야 한다
            // DB에서 받을 때 말곤 swipeRefreshLayout이랑 menu에서만 사용한다
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            return ViewModel(application) as T
        }
    }

    fun onCreate() {
        coinRepository.getCoinInfos().observeForever {
            coinInfos.value = it
        }
        coinRepository.getData("upbit")
        // exchange 저장하는 거 만들면 바꿔준다. 일단은 직접 지정.
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun updateCoinInfos(coinInfos: ArrayList<CoinInfo>) {
        this.coinInfos.value = coinInfos
    }

    fun println(data: String) {
        Log.d("ViewModel", data)
    }
}