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
    private val deListCoins = MutableLiveData<ArrayList<String>>()
    private val newListCoins = MutableLiveData<ArrayList<String>>()
    private var restartApp: Boolean
    private var exchange: String
    private val repository: Repository by lazy {
        Repository(application)
    }

    fun getCoinInfos() = coinInfos

    // 상장 폐지된 거랑 신규상장된 거
    fun getDeListCoins() = deListCoins

    fun getNewListCoins() = newListCoins

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
                        val coinInfosTemp = CoinInfosMaker.maker("coinone", it.body()!!)

                        if (restartApp) {
                            // 저장된 거에 있고 받아온 거에 없으면 폐지
                            // 저장된 거에 없고 받아온 거에 있으면 상장
                            // 두 놈 size가 같으면?
                            // containsAll 한 번 돌리고 false 나오면 전체 돌려야겠지
                            // oldCoinInfos.size > coinInfosTemp == 폐지
                            // oldCoinInfos.size < coinInfosTemp == 상장
                            val oldCoinInfos = repository.getAllByExchange(exchange)
                            
                            if (oldCoinInfos.size > coinInfosTemp.size) {
                                val deListCoinsTemp = ArrayList<String>()
                                for (i in oldCoinInfos.indices) {
                                    if (coinInfosTemp.contains(oldCoinInfos[i]))
                                        deListCoinsTemp.add(oldCoinInfos[i].coinName)
                                }
                                deListCoins.value = deListCoinsTemp
                            }
                            // 이게 맞나?
                            // 어떤 상황이든 대비해야 하잖아
                            // 사용자가 들어왔을 때 신규와 폐지가 아예 없을 수도 있고
                            // 신규 조금에 폐지 없거나 반대일 수도 있고
                            // 둘 다 넘칠 수도 있잖아
                            // 그냥 if 안 쓰고 비교하는 식으로 해야할 거 같은데    
                            // 존나 미친 생각이긴 한데
                            // 가로 리사이클러뷰 안에 세로 리사이클러뷰 2개를 넣는 거야
                            // 가로 사이즈가 1이면 isEmpty() == false인 리스트 맞춰서 텍스트뷰 바꿔주고
                            // 2면 둘 다 넣어주고
                            // 두 놈 다 isEmpty() == true면 그냥 안 띄우고
                            else {
                                val newListCoinsTemp = ArrayList<String>()
                                
                                for (i in coinInfosTemp.indices) {
                                    if (oldCoinInfos.contains(coinInfosTemp[i]))
                                        newListCoinsTemp.add(coinInfosTemp[i].coinName)
                                }
                                newListCoins.value = newListCoinsTemp
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

    fun delete(coinInfo: CoinInfo, next: () -> Unit) = addDisposable(repository.delete(coinInfo)
            .subscribe { next() })

    fun updateCoinInfos(coinInfos: ArrayList<CoinInfo>) {
        this.coinInfos.value = coinInfos
    }

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("ViewModel", data)
}