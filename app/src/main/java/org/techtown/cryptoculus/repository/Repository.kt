package org.techtown.cryptoculus.repository

import io.reactivex.Completable
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

    fun insert(coinInfo: CoinInfo): Completable

    fun insertAll(coinInfos: List<CoinInfo>): Completable

    fun updateCoinViewCheck(coinName: String): Completable

    fun updateCoinViewCheckAll(checkAll: Boolean): Completable

    fun updateClicked(coinName: String): Completable

    fun refreshClickedAll(exchange: String): Completable

    fun delete(coinInfo: CoinInfo): Completable

    fun getExchange(): String

    fun putExchange(exchange: String)

    fun getSortMode(): Int

    fun putSortMode(sortMode: Int)

    fun getIdleCheck(): Boolean

    fun putIdleCheck(idleCheck: Boolean)
}