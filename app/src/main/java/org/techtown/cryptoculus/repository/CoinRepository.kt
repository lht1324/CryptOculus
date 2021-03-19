package org.techtown.cryptoculus.repository

import android.app.Application
import org.techtown.cryptoculus.repository.model.CoinDao
import org.techtown.cryptoculus.repository.model.CoinDatabase
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.Client

class CoinRepository(application: Application) {
    private val coinDao: CoinDao by lazy { CoinDatabase.getInstance(application)!!.coinDao() }
    private val coinInfos: ArrayList<CoinInfo> by lazy { coinDao.getAll() as ArrayList<CoinInfo> }

    val coinone = "coinone"
    val bithumb = "bithumb"
    val upbit = "upbit"
    val huobi = "huobi"
    // coinone || bithumb || upbit || huobi
    // 이건 안 되나?
    // 4개 다 Boolean으로 만들고 or만 돌려주는 거지
    val client = Client()

    fun getCoinInfo(exchange: String, coinName: String): CoinInfo {
        return coinDao.getCoinInfo(exchange, coinName)
    }

    fun insert(coinInfo: CoinInfo) {
        coinDao.insert(coinInfo)
    }

    fun update(coinInfo: CoinInfo) {
        coinDao.update(coinInfo)
    }

    fun insertAll(coinInfos: ArrayList<CoinInfo>) {
        coinDao.insertAll(coinInfos)
    }

    fun updateAll(coinInfos: ArrayList<CoinInfo>) {
        coinDao.updateAll(coinInfos)
    }
}