package org.techtown.cryptoculus.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response

interface Repository {
    fun getData(exchange: String): Single<Response<Any>>

    fun getAllByExchange(exchange: String): ArrayList<CoinInfo>

    fun insert(coinInfo: CoinInfo): Observable<Unit>

    fun insertAll(coinInfos: ArrayList<CoinInfo>): Observable<Unit>

    fun update(coinInfo: CoinInfo): Observable<Unit>

    fun updateAll(coinInfos: ArrayList<CoinInfo>): Observable<Unit>

    fun delete(coinInfo: CoinInfo): Observable<Unit>
}