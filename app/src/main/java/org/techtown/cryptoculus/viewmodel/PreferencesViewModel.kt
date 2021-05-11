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
                ArrayList(coinInfos)
            }.subscribe(
                    {
                        coinInfos.value = it
                    },
                    { println("onFailure in getData(\"${exchange}\"): ${it.message}") }
            ))

    fun getCoinInfos() = coinInfos

    fun getCheckAll() = checkAll

    // 전체 체크 눌렀을 때
    @RequiresApi(Build.VERSION_CODES.N)
    fun updateCoinViewChecks(coinInfos: ArrayList<CoinInfo>, checkAll: Boolean): ArrayList<CoinInfo> {
        coinInfos.replaceAll {
            it.coinViewCheck = !checkAll
            it
        }
        updateAll(coinInfos)
        // coinInfos.value = savedCoinInfos
        // DB를 업데이트하면 그걸 자동으로 받아오는 게 되나?
        return coinInfos
    }

    // 하나만 체크 됐을 때
    fun updateCoinViewCheck(coinName: String) {
        val coinInfoTemp = getCoinInfo(exchange, coinName)
        coinInfoTemp.coinViewCheck = !coinInfoTemp.coinViewCheck
        update(coinInfoTemp)
    }

    fun updateCoinViewChecks() {
        val coinInfosTemp = repository.getAllByExchange(repository.getExchange())
        // 종료할 때 한꺼번에 받아온 다음에
        // 넘겨주는 건 안 돼?
        // 기본 정보는 coinInfosTemp에 있으니까 coinViewCheck만 전부 바꿔주면 되잖아
   }

    fun getCoinInfo(exchange: String, coinName: String) = repository.getCoinInfo(exchange, coinName)

    fun update(coinInfo: CoinInfo) = repository.update(coinInfo)

    fun updateAll(coinInfos: ArrayList<CoinInfo>) = repository.updateAll(coinInfos)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("PreferencesViewModel", data)
}