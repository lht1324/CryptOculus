package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.techtown.cryptoculus.repository.Repository
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class MainViewModel(application: Application) : ViewModel(){
    private val compositeDisposable = CompositeDisposable()
    private val news = MutableLiveData<ArrayList<Any>>()
    private var savedCoinInfos = ArrayList<CoinInfo>()
    private var optionCheckAll = false
    private var restartApp: Boolean
    private var exchange: String
    private val repository by lazy {
        RepositoryImpl(application)
    }
    
    fun getNews() = news

    fun getExchange() = exchange

    fun getSavedCoinInfos() = savedCoinInfos

    fun getOptionCheckAll() = optionCheckAll

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    init {
        this.exchange = application.getSharedPreferences("exchange", MODE_PRIVATE)
                .getString("exchange", "Coinone")!!
        this.restartApp = application.getSharedPreferences("restartApp", MODE_PRIVATE)
                .getBoolean("restartApp", false)
        // 전부 리포지토리로 옮기자
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    // map을 여기서 한 다음에 결과만 넘겨주면 안 돼?
    // 그냥 getData() 하나면 바로 실행되는 거잖아
    fun getData(exchange: String) = repository.getData(exchange)
            .map {
                this.exchange = exchange
                if (restartApp) {
                    val coinInfosOld = repository.getAllByExchange(exchange)
                    for (i in coinInfosOld.indices) {
                        if (i != coinInfosOld.size - 1 && !coinInfosOld[i].coinViewCheck) {
                            optionCheckAll = false
                            break
                        }
                        if (i == coinInfosOld.size - 1 && coinInfosOld[i].coinViewCheck) {
                            optionCheckAll = true
                        }
                    }
                }
                savedCoinInfos = CoinInfosMaker.maker(exchange, it.body()!!)
                CoinInfosMaker.maker(exchange, it.body()!!)
            }

    /* fun getData(exchange: String) = addDisposable(repository.getData(exchange)
            .subscribe(
                    {
                        this.exchange = exchange
                        var coinInfosNew = CoinInfosMaker.maker(exchange, it.body()!!)

                        // DB에서 나머지 싹 다 받아오고
                        // ticker만 바꿔주면 안 되나?
                        // CoinInfosMaker에서 coinInfos를 항상 새로 만드는 게 문제잖아
                        // ticker는 저장 안 하는 거니까 ticker만 새로 넣어주면 된다
                        if (restartApp) {
                            if (repository.getAllByExchange(exchange).isNotEmpty()) {
                                val coinInfosOld = repository.getAllByExchange(exchange)
                                val coinViewChecks = ArrayList<Boolean>()

                                for (i in coinInfosOld)
                                    coinViewChecks.add(i.coinViewCheck)

                                if (coinInfosOld.containsAll(coinInfosNew) || coinInfosNew.containsAll(coinInfosOld))
                                    compareCoinInfos(coinInfosNew, exchange)
                            }

                            updateAll(coinInfosNew) {
                                coinInfos.value = coinInfosNew
                                savedCoinInfos = coinInfosNew
                            }
                        }

                        else
                            insertAll(coinInfosNew) {
                                coinInfos.value = coinInfosNew
                                savedCoinInfos = coinInfosNew
                            }
                    },
                    { println("response error in getData(\"$exchange\") of ViewModel: ${it.message}") }
            )) */

    private fun compareCoinInfos(coinInfosNew: ArrayList<CoinInfo>, exchange: String) { // newsMaker란 이름이 맞나?
        val coinInfosOld = repository.getAllByExchange(exchange)
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

    fun refreshCoinInfos() = getData(exchange)

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateCoinViewChecks(checkAll: Boolean) {
        val coinInfosTemp = repository.getAllByExchange(exchange)
        coinInfosTemp.replaceAll {
            it.coinViewCheck = !checkAll
            it
        }
        updateAll(coinInfosTemp)
    }

    fun insert(coinInfo: CoinInfo) = addDisposable(repository.insert(coinInfo)
            .subscribe {  })

    private fun insertAll(coinInfos: ArrayList<CoinInfo>, next: () -> Unit) = addDisposable(repository.insertAll(coinInfos)
            .subscribe { next() })

    fun update(coinInfo: CoinInfo) = addDisposable(repository.update(coinInfo)
            .subscribe { })

    private fun updateAll(coinInfos: ArrayList<CoinInfo>) = addDisposable(repository.updateAll(coinInfos)
            .subscribe { })

    private fun delete(coinInfo: CoinInfo) = addDisposable(repository.delete(coinInfo)
            .subscribe { })

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("ViewModel", data)
}