package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class PreferencesViewModel(application: Application) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val checkAll = MutableLiveData<Boolean>()
    private var savedCoinInfos = ArrayList<CoinInfo>()
    private var exchange = repository.getExchange()

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PreferencesViewModel(application) as T
        }
    }

    init {
        getData()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    
    // 첫 실행이면 당연히 DB에 저장된 게 없다
    // 근데 첫 실행 아니어도 못 불러온다
    private fun getData() = addDisposable(repository.getAllByExchangeAsSingle(exchange)
            .map { coinInfos ->
                for (i in coinInfos.indices) {
                    if (i < coinInfos.size - 1 && !coinInfos[i].coinViewCheck) {
                        checkAll.value = false
                        break
                    }
                    if (i == coinInfos.size - 1 && coinInfos[i].coinViewCheck)
                        checkAll.value = true
                }
                savedCoinInfos = ArrayList(coinInfos)
                ArrayList(coinInfos)
            }.subscribe(
                    {
                        println("coinInfos.size = ${it.size}")
                        coinInfos.value = it
                    },
                    { println("onFailure in getData(\"${exchange}\"): ${it.message}") }
            ))

    fun getCoinInfos() = coinInfos

    fun getCheckAll() = checkAll

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateCoinViewChecks(checkAll: Boolean) {
        savedCoinInfos.replaceAll {
            it.coinViewCheck = !checkAll
            it
        }
        updateAll(savedCoinInfos)
        coinInfos.value = savedCoinInfos
    }

    fun updateCoinViewCheck(coinName: String) {
        val coinInfoTemp = getCoinInfo(exchange, coinName)
        coinInfoTemp.coinViewCheck = !coinInfoTemp.coinViewCheck
        update(coinInfoTemp)
    }

    fun getCoinInfo(exchange: String, coinName: String) = repository.getCoinInfo(exchange, coinName)

    fun update(coinInfo: CoinInfo) = repository.update(coinInfo)

    fun updateAll(coinInfos: ArrayList<CoinInfo>) = repository.updateAll(coinInfos)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("PreferencesViewModel", data)
}