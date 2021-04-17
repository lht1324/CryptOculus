package org.techtown.cryptoculus.repository

import android.app.Application
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.model.CoinInfoDatabase
import org.techtown.cryptoculus.repository.network.Client

class Repository(application: Application) {
    private val coinInfoDao = CoinInfoDatabase.getInstance(application)!!.coinInfoDao()
    private val client: Client by lazy {
        Client()
    }

    fun getDataCoinone() = client.getDataCoinone()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getDataBithumb() = client.getDataBithumb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getDataUpbit() = client.getDataUpbit()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getDataHuobi() = client.getDataHuobi()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}