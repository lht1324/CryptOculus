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
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val news = MutableLiveData<ArrayList<Any>>()
    private var restartApp: Boolean
    private var exchange: String
    private val repository: Repository by lazy {
        Repository(application)
    }

    fun getCoinInfos() = coinInfos
    
    fun getNews() = news

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
    // getNews는 따로 만들자
    // 위에 버튼 중에 시스템은 never로 하고 거래소는 스피너에 넣어버리면 공간 나올 거야
    // 스피너 왼쪽으로 몰아버리면 never 안 쓰고 always 써도 될 지도 모르고

    fun getDataCoinone() = addDisposable(repository.getDataCoinone()
            .subscribe(
                    {
                        exchange = "coinone"
                        val coinInfosTemp = CoinInfosMaker.maker("coinone", it.body()!!)

                        if (restartApp) {
                            // size가 같으면 둘 중 하나다
                            // 업데이트가 없거나 같은 개수가 폐지되고 상장되었거나
                            val oldCoinInfos = repository.getAllByExchange(exchange)
                            val newsTemp = ArrayList<Any>()

                            // if (coinInfosTemp.containsAll(oldCoinInfos) || oldCoinInfos.containsAll(coinInfosTemp))
                            if (coinInfosTemp.containsAll(oldCoinInfos)) { // 신규 상장
                                    
                                val newListCoinsTemp = ArrayList<String>()

                                for (i in coinInfosTemp.indices) {
                                    if (!oldCoinInfos.contains(coinInfosTemp[i]))
                                        newListCoinsTemp.add(coinInfosTemp[i].coinName)
                                }

                                newsTemp.add(newListCoinsTemp)
                            }

                            if (oldCoinInfos.containsAll(coinInfosTemp)) { // 상장 폐지
                                val deListCoinsTemp = ArrayList<String>()

                                for (i in oldCoinInfos.indices) {
                                    if (coinInfosTemp.contains(oldCoinInfos[i])) {
                                        deListCoinsTemp.add(oldCoinInfos[i].coinName)
                                        delete(oldCoinInfos[i])
                                    }
                                }

                                newsTemp.add(deListCoinsTemp)

                                // 이 안에서 newsTemp.size == 1이면 폐지만 됐다는 뜻이다
                                if (newsTemp.isNotEmpty() && newsTemp.size == 1) // 상장 폐지
                                    newsTemp.add(0, "deList")
                            }

                            // old만 받았다면 size는 2다
                            // 겹치네
                            if (newsTemp.isNotEmpty()) {
                                if (newsTemp.size == 1)
                                    newsTemp.add(0, "newList")
                                if (newsTemp.size == 2 && newsTemp[0] != "deList")
                                    newsTemp.add(0, "both")

                                news.value = newsTemp
                            }

                            updateAll(coinInfosTemp) { coinInfos.value = coinInfosTemp }
                        }
                        else
                            insertAll(coinInfosTemp) { coinInfos.value = coinInfosTemp }
                    },
                    { println("response error in getDataCoinone() of ViewModel: ${it.message}")}
            ))

    fun getDataBithumb() = addDisposable(repository.getDataBithumb()
            .subscribe(
                    {
                        exchange = "bithumb"
                        val coinInfosTemp = CoinInfosMaker.maker("bithumb", it.body()!!)

                        if (restartApp)
                            updateAll(coinInfosTemp) { coinInfos.value = coinInfosTemp }
                        else
                            insertAll(coinInfosTemp) { coinInfos.value = coinInfosTemp}
                    },
                    { println("response error in getDataBithumb() of ViewModel: ${it.message}")}
            ))

    fun getDataUpbit() = addDisposable(repository.getDataUpbit()
            .subscribe(
                    {
                        exchange = "upbit"
                        val coinInfosTemp = CoinInfosMaker.maker("upbit", it.body()!!)

                        if (restartApp)
                            updateAll(coinInfosTemp) { coinInfos.value = coinInfosTemp }
                        else
                            insertAll(coinInfosTemp) { coinInfos.value = coinInfosTemp}
                    },
                    { println("response error in getDataUpbit() of ViewModel: ${it.message}")}
            ))

    fun getDataHuobi() = addDisposable(repository.getDataHuobi()
            .subscribe(
                    {
                        exchange = "huobi"
                        val coinInfosTemp = CoinInfosMaker.maker("huobi", it.body()!!)

                        if (restartApp)
                            updateAll(coinInfosTemp) { coinInfos.value = coinInfosTemp }
                        else
                            insertAll(coinInfosTemp) { coinInfos.value = coinInfosTemp}
                        // coinInfos.value = CoinInfosMaker.maker("huobi", it.body()!!)
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

    fun insert(coinInfo: CoinInfo, next: () -> Unit) = addDisposable(repository.insert(coinInfo)
            .subscribe { next() })

    fun insertAll(coinInfos: ArrayList<CoinInfo>, next: () -> Unit) = addDisposable(repository.insertAll(coinInfos)
            .subscribe { next() })

    fun update(coinInfo: CoinInfo, next: () -> Unit) = addDisposable(repository.update(coinInfo)
            .subscribe { next() })

    fun updateAll(coinInfos: ArrayList<CoinInfo>, next: () -> Unit) = addDisposable(repository.updateAll(coinInfos)
            .subscribe { next() })

    fun delete(coinInfo: CoinInfo) = addDisposable(repository.delete(coinInfo)
            .subscribe { })

    fun updateCoinInfos(coinInfos: ArrayList<CoinInfo>) {
        this.coinInfos.value = coinInfos
    }

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("ViewModel", data)
}