package org.techtown.cryptoculus.repository

import android.app.Application
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.model.*
import org.techtown.cryptoculus.repository.network.Client

class RepositoryImpl(private val application: Application) : Repository{
    private val coinInfoDao: CoinInfoDao by lazy {
        CoinInfoDatabase.getInstance(application)!!.coinInfoDao()
    }
    // MODE_PRIVATE
    private val preferences by lazy {
        SavedSharedPreferencesImpl(application.getSharedPreferences("preferences", 0))
    }

    override fun getData(exchange: String) = Client().getData(exchange)

    override fun getAllByExchangeAsSingle(exchange: String) = coinInfoDao.getAllByExchangeAsSingle(exchange)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getAllByExchange(exchange: String) = coinInfoDao.getAllByExchange(exchange)

    override fun getCoinInfo(exchange: String, coinName: String) = coinInfoDao.getCoinInfo(exchange, coinName)

    override fun insert(coinInfo: CoinInfo) = coinInfoDao.insert(coinInfo)

    override fun insertAll(coinInfos: List<CoinInfo>) = coinInfoDao.insertAll(coinInfos)

    override fun update(coinInfo: CoinInfo) = coinInfoDao.update(coinInfo)

    override fun updateAll(coinInfos: List<CoinInfo>) = coinInfoDao.updateAll(coinInfos)

    override fun delete(coinInfo: CoinInfo) = coinInfoDao.delete(coinInfo)

    override fun getRestartApp() = preferences.getRestartApp()

    override fun putRestartApp(restartApp: Boolean) = preferences.putRestartApp(restartApp)

    override fun getExchange() = preferences.getExchange()

    override fun putExchange(exchange: String) = preferences.putExchange(exchange)

    override fun getSortMode() = preferences.getSortMode()

    override fun putSortMode(sortMode: Int) = preferences.putSortMode(sortMode)

    private fun template(argFun: () -> Unit) = Observable.fromCallable { argFun() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}