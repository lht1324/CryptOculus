package org.techtown.cryptoculus.repository.model

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface CoinInfoDao {
    @Query("SELECT * FROM coinInfoTable")
    fun getAll(): List<CoinInfo>

    // getAllByExchange를 observe?
    @Query("Select * from coinInfoTable where exchange is :exchange")
    fun getAllByExchange(exchange: String): ArrayList<CoinInfo>

    @Query("Select * from coinInfoTable where exchange is :exchange and coinName is :coinName")
    fun getToDo(exchange: String, coinName: String): CoinInfo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coinInfo: CoinInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(coinInfos: List<CoinInfo>)

    // coinViewCheck 업데이트 됐을 때 쓰겠지
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(coinInfo: CoinInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(coinInfos: ArrayList<CoinInfo>)

    // 상장폐지 되었을 때 써야 한다
    // exchange와 coinName이 있는데 새로 받은 거에 exchange 있고 coinName이 없을 때?
    @Delete
    fun delete(coinInfo: CoinInfo)
}