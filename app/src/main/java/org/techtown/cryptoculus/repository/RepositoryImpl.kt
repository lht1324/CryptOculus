package org.techtown.cryptoculus.repository

import android.app.Application
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.model.CoinInfoDao
import org.techtown.cryptoculus.repository.model.CoinInfoDatabase
import org.techtown.cryptoculus.repository.network.Client

class RepositoryImpl(application: Application) : Repository{
    private val coinInfoDao: CoinInfoDao by lazy {
        CoinInfoDatabase.getInstance(application)!!.coinInfoDao()
    }
    override fun getData(exchange: String) = Client().getData(exchange)

    override fun getAllByExchange(exchange: String) = coinInfoDao.getAllByExchange(exchange)

    override fun insert(coinInfo: CoinInfo) = template { coinInfoDao.insert(coinInfo) }

    override fun insertAll(coinInfos: ArrayList<CoinInfo>) = template { coinInfoDao.insertAll(coinInfos) }

    override fun update(coinInfo: CoinInfo) = template { coinInfoDao.update(coinInfo) }

    override fun updateAll(coinInfos: ArrayList<CoinInfo>) = template { coinInfoDao.updateAll(coinInfos) }

    override fun delete(coinInfo: CoinInfo) = template { coinInfoDao.delete(coinInfo) }

    private fun template(argFun: () -> Unit) = Observable.fromCallable { argFun() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}