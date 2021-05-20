package org.techtown.cryptoculus.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response

interface Repository {
    fun getData(exchange: String): Single<Response<Any>>

    fun getAllByExchangeAsSingle(exchange: String): Single<List<CoinInfo>>

    fun getAllByExchange(exchange: String): List<CoinInfo>

    fun getCoinInfo(exchange: String, coinName: String): CoinInfo

    fun insert(coinInfo: CoinInfo)

    fun insertAll(coinInfos: List<CoinInfo>)

    fun update(coinInfo: CoinInfo)

    fun updateAll(coinInfos: List<CoinInfo>)

    fun delete(coinInfo: CoinInfo)

    fun getExchange(): String

    fun putExchange(exchange: String)

    fun getSortMode(): Int

    fun putSortMode(sortMode: Int)
}