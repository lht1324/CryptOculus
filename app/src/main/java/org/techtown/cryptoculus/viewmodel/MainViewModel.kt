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

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    fun getCoinInfos() = coinInfos

    fun getNews() = news

    fun getSortMode() = repository.getSortMode()

    init {
        getData()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        if (!repository.getRestartApp())
            repository.putRestartApp(true)
        super.onCleared()
    }

    fun getData() {
        val exchange = repository.getExchange()

        addDisposable(repository.getData(exchange)
            .map {
                val coinInfosNew = CoinInfosMaker.maker(exchange, it.body()!!)

                // News
                if (repository.getRestartApp() && repository.getAllByExchange(exchange).isNotEmpty()) {
                    val coinInfosOld = ArrayList(repository.getAllByExchange(exchange))
                    val coinViewChecks = ArrayList<Boolean>()

                    for (i in coinInfosOld)
                        coinViewChecks.add(i.coinViewCheck)

                    if (coinInfosOld.containsAll(coinInfosNew) || coinInfosNew.containsAll(coinInfosOld))
                        compareCoinInfos(coinInfosNew, exchange)
                }

                coinInfosNew
            }.subscribe(
                {
                    if (repository.getRestartApp() && repository.getAllByExchange(exchange).isNotEmpty())
                        updateAll(it.toList()) { coinInfos.value = it }
                    else
                        insertAll(it.toList()) { coinInfos.value = it }
                },
                { println("response error in getData(\"${exchange}\"): ${it.message}") }
            ))
    }

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

    fun changeExchange(position: Int) {
        repository.putExchange(when (position) {
            0 -> "Coinone"
            1 -> "Bithumb"
            else -> "Upbit" // 2
        })
        getData()
    }

    fun updateSortMode(sortMode: Int) {
        putSortMode(sortMode)
        getData()
    }

    fun getSelection() = when (repository.getExchange()) {
        "Coinone" -> 0
        "Bithumb" -> 1
        else -> 2
    }

    private fun putRestartApp(restartApp: Boolean) = repository.putRestartApp(restartApp)

    private fun putSortMode(sortMode: Int) = repository.putSortMode(sortMode)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("MainViewModel", data)
}