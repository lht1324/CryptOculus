package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class MainViewModel(application: Application) : ViewModel(){
    private val compositeDisposable = CompositeDisposable()
    private val news = MutableLiveData<ArrayList<Any>>()
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private var restartApp = repository.getRestartApp()
    private var exchange = repository.getExchange()
    private var sortMode = repository.getExchange() // 얘도 저장해야 해

    fun getCoinInfos() = coinInfos

    fun getNews() = news

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        putExchange()
        if (!restartApp)
            putRestartApp()
        super.onCleared()
    }

    private fun getData(exchange: String) = addDisposable(repository.getData(exchange)
            .map {
                this.exchange = exchange
                val coinInfosNew = CoinInfosMaker.maker(exchange, it.body()!!)

                if (restartApp) {
                    // News
                    if (repository.getAllByExchange(exchange).isNotEmpty()) {
                        val coinInfosOld = ArrayList(repository.getAllByExchange(exchange))
                        val coinViewChecks = ArrayList<Boolean>()

                        for (i in coinInfosOld)
                            coinViewChecks.add(i.coinViewCheck)

                        if (coinInfosOld.containsAll(coinInfosNew) || coinInfosNew.containsAll(coinInfosOld))
                            compareCoinInfos(coinInfosNew, exchange)
                    }
                }
                coinInfosNew.sortWith { o1, o2 ->
                    // o1.ticker.changeRate.toDouble().compareTo(o2.ticker.changeRate.toDouble())
                    o2.ticker.changeRate.toDouble().compareTo(o1.ticker.changeRate.toDouble())
                }
                // 이름, 현재가
                // savedCoinInfos = CoinInfosMaker.maker(exchange, it.body()!!)
                /* coinInfosNew.sortWith { o1, o2 ->
                    o1.coinName.compareTo(o2.coinName) // 오름차순
                    // o2.coinName.compareTo(o1.coinName) // 내림차순
                }
                coinInfosNew.sortWith { o1, o2 ->
                    // o1.ticker.changeRate.toDouble().compareTo(o2.ticker.changeRate.toDouble())
                    o2.ticker.changeRate.toDouble().compareTo(o1.ticker.changeRate.toDouble())
                }
                coinInfosNew.sortWith { o1, o2 ->
                    // o1.ticker.lastInTicker.replace(",", "").toDouble().compareTo(o2.ticker.lastInTicker.replace(",", "").toDouble()) // 오름차순
                    o2.ticker.lastInTicker.replace(",", "").toDouble().compareTo(o1.ticker.lastInTicker.replace(",", "").toDouble()) // 내림차순
                } */
                coinInfosNew
            }.subscribe(
                    {
                        // restartApp만 하면 되는 게 아니라 DB가 비었는지도 알아야 한다
                        if (restartApp && repository.getAllByExchange(exchange).isNotEmpty())
                            updateAll(it.toList()) { coinInfos.value = it }
                        else
                            insertAll(it.toList()) { coinInfos.value = it }
                    },
                    { println("response error in getData(\"${exchange}\"): ${it.message}") }
            ))

    private fun compareCoinInfos(coinInfosNew: ArrayList<CoinInfo>, exchange: String) {
        val coinInfosOld = ArrayList(repository.getAllByExchange(exchange))
        val newListing = coinInfosNew.containsAll(coinInfosOld)
        val deListing = coinInfosOld.containsAll(coinInfosNew)
        val newsTemp = ArrayList<Any>()
        val coinList = ArrayList<String>()

        if (newListing) { // 신규 상장
            for (i in coinInfosNew.indices) {
                if (!coinInfosOld.contains(coinInfosNew[i])) {
                    coinList.add(coinInfosNew[i].coinName)
                    insert(coinInfosNew[i])
                }
            }

            newsTemp.add(coinList)
        }

        if (deListing) { // 상장 폐지
            if (coinList.isNotEmpty()) // 신규상장 기록하느라 coinList를 사용했으면 재활용을 위해 비워주기
                coinList.clear()

            for (i in coinInfosOld.indices) {
                if (coinInfosNew.contains(coinInfosOld[i])) {
                    coinList.add(coinInfosOld[i].coinName)
                    delete(coinInfosOld[i])
                }
            }

            newsTemp.add(coinList)

            if (newsTemp.size == 1) // 신규 상장 없고 상장 폐지만 있을 때
                newsTemp.add(0, "deList")
        }

        if (newsTemp.size == 1) // 신규 상장만 있고 상장 폐지 없을 때
            newsTemp.add(0, "newList")
        if (newsTemp.size == 2 && newsTemp[0] != "deList") // 신규 상장 없고 상장 폐지만 있어도 newsTemp.size는 2다
            newsTemp.add(0, "both")

        news.value = newsTemp
    }

    private fun insert(coinInfo: CoinInfo) = addDisposable(repository.insert(coinInfo)
            .subscribe {  })

    private fun insertAll(coinInfos: List<CoinInfo>, next: () -> Unit) = addDisposable(repository.insertAll(coinInfos)
            .subscribe { next() })

    fun update(coinInfo: CoinInfo) = addDisposable(repository.update(coinInfo)
            .subscribe { })

    private fun updateAll(coinInfos: List<CoinInfo>, next: () -> Unit) = addDisposable(repository.updateAll(coinInfos)
            .subscribe { next() })

    private fun delete(coinInfo: CoinInfo) = addDisposable(repository.delete(coinInfo)
            .subscribe { })

    fun refreshCoinInfos() = getData(exchange)

    fun changeExchange(position: Int) {
        exchange = when (position) {
            0 -> "Coinone"
            1 -> "Bithumb"
            else -> "Upbit" // 2
        }
        putExchange()
        getData(exchange)
    }

    fun getSelection() = when (exchange) {
        "Coinone" -> 0
        "Bithumb" -> 1
        else -> 2
    }

    private fun putExchange() = repository.putExchange(exchange)

    private fun putRestartApp() = repository.putRestartApp(true)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("MainViewModel", data)
}