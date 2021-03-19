package org.techtown.cryptoculus.repository.model

import androidx.room.*
import java.util.*

@Dao
interface CoinDao {
    @Query("Select * from coinInfoTable")
    fun getAll(): List<CoinInfo>

    @Query("Select * from coinInfoTable where exchange and coinName is :exchange and :coinName")
    fun getCoinInfo(exchange: String, coinName: String): CoinInfo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coinInfo: CoinInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(coinInfos: ArrayList<CoinInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(coinInfo: CoinInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(coinInfos: ArrayList<CoinInfo>)
}