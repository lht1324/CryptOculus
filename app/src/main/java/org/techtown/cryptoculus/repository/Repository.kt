package org.techtown.cryptoculus.repository

import android.app.Application
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.model.CoinInfoDao
import org.techtown.cryptoculus.repository.model.CoinInfoDatabase
import org.techtown.cryptoculus.repository.network.Client
import retrofit2.Response

class Repository(application: Application) {
    private val coinInfoDao: CoinInfoDao by lazy {
        CoinInfoDatabase.getInstance(application)!!.coinInfoDao()
    }
    private val client: Client by lazy {
        Client()
    }

    fun getData(exchange: String): Single<Response<Any>> = client.getData(exchange)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /* fun getAllByExchange(exchange: String) = Observable.fromCallable { coinInfoDao.getAllByExchange(exchange) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insert(coinInfo: CoinInfo) = Observable.fromCallable { coinInfoDao.insert(coinInfo) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertAll(coinInfos: ArrayList<CoinInfo>) = Observable.fromCallable { coinInfoDao.insertAll(coinInfos) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun update(coinInfo: CoinInfo) = Observable.fromCallable { coinInfoDao.update(coinInfo) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateAll(coinInfos: ArrayList<CoinInfo>) = Observable.fromCallable { coinInfoDao.updateAll(coinInfos) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun delete(coinInfo: CoinInfo) = Observable.fromCallable { coinInfoDao.delete(coinInfo) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) */

    fun getAllByExchange(exchange: String) = ArrayList<CoinInfo>(coinInfoDao.getAllByExchange(exchange))

    fun insert(coinInfo: CoinInfo) = templateDao { coinInfoDao.insert(coinInfo) }

    fun insertAll(coinInfos: ArrayList<CoinInfo>) = templateDao { coinInfoDao.insertAll(coinInfos) }

    fun update(coinInfo: CoinInfo) = templateDao { coinInfoDao.update(coinInfo) }

    fun updateAll(coinInfos: ArrayList<CoinInfo>) = templateDao { coinInfoDao.updateAll(coinInfos) }

    fun delete(coinInfo: CoinInfo) = templateDao { coinInfoDao.delete(coinInfo) }

    private fun templateDao(argFun: () -> Unit) = Observable.fromCallable { argFun() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private fun templateResponse(response: Single<Any>) = response.
            subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    // 필요하냐?
    // 바뀐 것만 실시간으로 업데이트 해주는 거 아녔어?
    // fun insertAll(coinInfos: CoinInfo) = Observable.fromCallable { coinInfoDao.insertAll(coinInfos.toList()) }
}