package org.techtown.cryptoculus.repository.model

import androidx.room.*
import androidx.room.Dao
import io.reactivex.Single

@Dao
interface CoinInfoDao {
    @Query("Select * from coinInfoTable where exchange is :exchange")
    fun getAllByExchangeAsSingle(exchange: String): Single<List<CoinInfo>>

    @Query("Select * from coinInfoTable where exchange is :exchange")
    fun getAllByExchange(exchange: String): List<CoinInfo>

    @Query("Select * from coinInfoTable where exchange is :exchange and coinName is :coinName")
    fun getCoinInfo(exchange: String, coinName: String): CoinInfo

    @Query("Select coinViewCheck from coinInfoTable where exchange is :exchange and coinName is :coinName")
    fun getCoinViewCheck(exchange: String, coinName: String): Boolean

    @Query("Select coinViewCheck from coinInfoTable where exchange is :exchange")
    fun getCoinViewChecks(exchange: String): List<Boolean>

    @Query("Select clicked from coinInfoTable where exchange is :exchange and coinName is :coinName")
    fun getClicked(exchange: String, coinName: String): Boolean

    @Insert
    fun insert(coinInfo: CoinInfo)

    @Insert
    fun insertAll(coinInfos: List<CoinInfo>)

    // coinViewCheck 업데이트 됐을 때 쓰겠지
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(coinInfo: CoinInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(coinInfos: List<CoinInfo>)

    @Query("Update coinInfoTable set coinViewCheck=:coinViewCheck where exchange is :exchange and coinName is :coinName")
    fun updateCoinViewCheck(coinViewCheck: Boolean, exchange: String, coinName: String)

    @Query("Update coinInfoTable set coinViewCheck=:coinViewCheck where exchange is :exchange")
    fun updateCoinViewCheckAll(coinViewCheck: Boolean, exchange: String)

    @Query("Update coinInfoTable set clicked=:clicked where exchange is :exchange and coinName is :coinName")
    fun updateClicked(clicked: Boolean, exchange: String, coinName: String)

    @Query("Update coinInfoTable set clicked=:clicked where exchange is :exchange")
    fun updateClickedAll(clicked: Boolean, exchange: String)

    @Query("Update coinInfoTable set clicked=0 where exchange is :exchange")
    fun refreshClickedAll(exchange: String)

    @Delete
    fun delete(coinInfo: CoinInfo)
}