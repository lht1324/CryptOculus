package org.techtown.cryptoculus.repository.model

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface CoinDao {
    @Query("Select * from coinInfoTable")
    fun getAll(): LiveData<List<CoinInfo>>

    @Query("Select * from coinInfoTable where exchange and coinName is :exchange and :coinName")
    fun getCoinInfo(exchange: String, coinName: String): LiveData<CoinInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coinInfo: CoinInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(coinInfos: List<CoinInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(coinInfo: CoinInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(coinInfos: List<CoinInfo>)
}