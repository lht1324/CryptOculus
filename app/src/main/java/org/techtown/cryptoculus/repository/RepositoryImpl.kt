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
    private val restartAppPreferences by lazy {
        application.getSharedPreferences("restartApp", 0)
    }
    private val exchangePreferences by lazy {
        application.getSharedPreferences("exchange", 0)
    }

    override fun getData(exchange: String) = Client().getData(exchange)

    override fun getAllByExchangeAsSingle(exchange: String) = coinInfoDao.getAllByExchangeAsSingle(exchange)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getAllByExchange(exchange: String) = coinInfoDao.getAllByExchange(exchange)

    override fun getCoinInfo(exchange: String, coinName: String) = coinInfoDao.getCoinInfo(exchange, coinName)

    override fun insert(coinInfo: CoinInfo) = template { coinInfoDao.insert(coinInfo) }

    override fun insertAll(coinInfos: List<CoinInfo>) = template { coinInfoDao.insertAll(coinInfos) }

    override fun update(coinInfo: CoinInfo) = template { coinInfoDao.update(coinInfo) }

    override fun updateAll(coinInfos: List<CoinInfo>) = template { coinInfoDao.updateAll(coinInfos) }

    override fun delete(coinInfo: CoinInfo) = template { coinInfoDao.delete(coinInfo) }

    override fun getRestartApp() = Preferences(restartAppPreferences).getRestartApp()

    override fun putRestartApp(restartApp: Boolean) = Preferences(restartAppPreferences).putRestartApp(restartApp)

    override fun getExchange() = Preferences(exchangePreferences).getExchange()

    override fun putExchange(exchange: String) = Preferences(exchangePreferences).putExchange(exchange)

    private fun template(argFun: () -> Unit) = Observable.fromCallable { argFun() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}