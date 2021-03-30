package org.techtown.cryptoculus.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.RetrofitClient

class CoinRepository(val application: Application) {
    private val client = RetrofitClient
    val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()

    @JvmName("getCoinInfos1")
    fun getCoinInfos() = coinInfos

    fun onCreate() {
        client.getCoinInfos().observeForever {
            coinInfos.value = it
        }
    }

    fun getData(exchange: String) {
        RetrofitClient.getData(exchange)
    }

    /*
    fun insert(coinInfo: CoinInfo): Observable<Unit> {
        return Observable.fromCallable { coinDao.insert(coinInfo) }
    }
     */
}