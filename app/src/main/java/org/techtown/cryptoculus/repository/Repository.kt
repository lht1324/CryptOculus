package org.techtown.cryptoculus.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response

interface Repository {
    fun getData(): Single<Response<Any>>

    fun getAllAsSingle(): Single<List<CoinInfo>>

    fun getAll(): List<CoinInfo>

    fun getAllByExchange(exchange: String): List<CoinInfo>

    fun getCoinInfo(coinName: String): CoinInfo

    fun getCoinViewCheck(coinName: String): Boolean

    fun getCoinViewChecks(): List<Boolean>

    fun getClicked(coinName: String): Boolean

    fun insert(coinInfo: CoinInfo)

    fun insertAll(coinInfos: List<CoinInfo>)

    fun update(coinInfo: CoinInfo)

    fun updateAll(coinInfos: List<CoinInfo>)

    fun updateCoinViewCheck(coinName: String)

    fun updateCoinViewCheckAll(checkAll: Boolean)

    fun updateClicked(coinName: String)

    fun refreshClickedAll(exchange: String)

    fun delete(coinInfo: CoinInfo)

    fun getExchange(): String

    fun putExchange(exchange: String)

    fun getSortMode(): Int

    fun putSortMode(sortMode: Int)

    fun getIdleCheck(): Boolean

    fun putIdleCheck(idleCheck: Boolean)
}