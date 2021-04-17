package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.techtown.cryptoculus.pojo.Ticker
import org.techtown.cryptoculus.pojo.TickerCoinone
import org.techtown.cryptoculus.repository.Repository
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response

class ViewModel(exchange: String, restartApp: Boolean, application: Application) : ViewModel(){
    private val compositeDisposable = CompositeDisposable()
    private var coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private var restartApp: Boolean
    private var exchange: String
    private val repository: Repository by lazy {
        Repository(application)
    }

    fun getCoinInfos() = coinInfos

    fun getExchange() = exchange

    class Factory(
            private val exchange: String,
            private val restartApp: Boolean,
            private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ViewModel(exchange, restartApp, application) as T
        }
    }

    init {
        this.exchange = exchange
        this.restartApp = restartApp

        when (exchange) {
            "coinone" -> getDataCoinone()
            "bithumb" -> getDataBithumb()
            "upbit" -> getDataUpbit()
            "huobi" -> getDataHuobi()
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getDataCoinone() = addDisposable(repository.getDataCoinone()
            .subscribe(
                    {
                        exchange = "coinone"
                        coinInfos.value = CoinInfosMaker.maker("coinone", it.body()!!)
                    },
                    { println("response error in getDataCoinone() of ViewModel: ${it.message}")}
            ))

    fun getDataBithumb() = addDisposable(repository.getDataBithumb()
            .subscribe(
                    {
                        exchange = "bithumb"
                        coinInfos.value = CoinInfosMaker.maker("bithumb", it.body()!!)
                    },
                    { println("response error in getDataBithumb() of ViewModel: ${it.message}")}
            ))

    fun getDataUpbit() = addDisposable(repository.getDataUpbit()
            .subscribe(
                    {
                        exchange = "upbit"
                        coinInfos.value = CoinInfosMaker.maker("upbit", it.body()!!)
                    },
                    { println("response error in getDataUpbit() of ViewModel: ${it.message}")}
            ))

    fun getDataHuobi() = addDisposable(repository.getDataHuobi()
            .subscribe(
                    {
                        exchange = "huobi"
                        coinInfos.value = CoinInfosMaker.maker("huobi", it.body()!!)
                    },
                    { println("response error in getDataHuobi() of ViewModel: ${it.message}")}
            ))

    fun refreshCoinInfos() {
        when (exchange) {
            "coinone" -> getDataCoinone()
            "bithumb" -> getDataBithumb()
            "upbit" -> getDataUpbit()
            "huobi" -> getDataHuobi()
        }
    }

    fun updateCoinInfos(coinInfos: ArrayList<CoinInfo>) {
        this.coinInfos.value = coinInfos
    }

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("ViewModel", data)
}