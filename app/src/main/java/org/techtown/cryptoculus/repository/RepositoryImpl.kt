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

    override fun getData() = Client().getData(getExchange())

    override fun getAllByExchange(exchange: String) = coinInfoDao.getAllByExchange(exchange)

    override fun getAllAsSingle() = coinInfoDao.getAllByExchangeAsSingle(getExchange())

    override fun getAll() = coinInfoDao.getAllByExchange(getExchange())

    override fun getCoinInfo(coinName: String) = coinInfoDao.getCoinInfo(getExchange(), coinName)

    override fun getCoinViewCheck(coinName: String) = coinInfoDao.getCoinViewCheck(getExchange(), coinName)

    override fun getCoinViewChecks() = coinInfoDao.getCoinViewChecks(getExchange())

    override fun getClicked(coinName: String) = coinInfoDao.getClicked(getExchange(), coinName)

    override fun insert(coinInfo: CoinInfo) = coinInfoDao.insert(coinInfo)

    override fun insertAll(coinInfos: List<CoinInfo>) = coinInfoDao.insertAll(coinInfos)

    override fun update(coinInfo: CoinInfo) = coinInfoDao.update(coinInfo)

    override fun updateAll(coinInfos: List<CoinInfo>) = coinInfoDao.updateAll(coinInfos)

    override fun updateCoinViewCheck(coinName: String) = coinInfoDao.updateCoinViewCheck(!coinInfoDao.getCoinViewCheck(getExchange(), coinName), getExchange(), coinName)

    override fun updateCoinViewCheckAll(checkAll: Boolean) = coinInfoDao.updateCoinViewCheckAll(checkAll, getExchange())

    override fun updateClicked(coinName: String) = coinInfoDao.updateClicked(!coinInfoDao.getClicked(getExchange(), coinName), getExchange(), coinName)

    override fun refreshClickedAll(exchange: String) = coinInfoDao.refreshClickedAll(exchange)

    override fun delete(coinInfo: CoinInfo) = coinInfoDao.delete(coinInfo)

    override fun getExchange() = preferences.getExchange()

    override fun putExchange(exchange: String) = preferences.putExchange(exchange)

    override fun getSortMode() = preferences.getSortMode()

    override fun putSortMode(sortMode: Int) = preferences.putSortMode(sortMode)

    override fun getIdleCheck() = preferences.getIdleCheck()

    override fun putIdleCheck(idleCheck: Boolean) = preferences.putIdleCheck(idleCheck)
}