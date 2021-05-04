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

    fun insert(coinInfo: CoinInfo): Observable<Unit>

    fun insertAll(coinInfos: List<CoinInfo>): Observable<Unit>

    fun update(coinInfo: CoinInfo): Observable<Unit>

    fun updateAll(coinInfos: List<CoinInfo>): Observable<Unit>

    fun delete(coinInfo: CoinInfo): Observable<Unit>

    fun getRestartApp(): Boolean

    fun getExchange(): String

    fun putRestartApp(restartApp: Boolean)

    fun putExchange(exchange: String)
}