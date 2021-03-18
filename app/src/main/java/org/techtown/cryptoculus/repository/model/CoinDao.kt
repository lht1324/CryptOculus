package org.techtown.cryptoculus.repository.model

import androidx.room.*
import java.util.*

@Dao
interface CoinDao {
    @Query("Select * from coinInfoTable")
    fun getAll(): List<CoinInfo>

    @Query("Select * from coinInfoTable where id is :inputId")
    fun getCoinInfo(inputId: Int): CoinInfo

    @Query("Select * from coinInfoTable where coinName is :coinName and coinViewCheck is :coinViewCheck")
    fun getId(coinName: String, coinViewCheck: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coinInfo: CoinInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(coinInfos: ArrayList<CoinInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(coinInfo: CoinInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(coinInfos: ArrayList<CoinInfo>)
}