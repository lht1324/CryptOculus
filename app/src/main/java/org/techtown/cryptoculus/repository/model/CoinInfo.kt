package org.techtown.cryptoculus.repository.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.techtown.cryptoculus.pojo.Ticker

@Entity(tableName = "coinInfoTable", primaryKeys = ["exchange", "coinName"])
class CoinInfo {
    var exchange = "" // 거래소
    var coinName = "" // 가공 후 저장되는 종목 코드
    var coinViewCheck = true
    var clicked = false
    @Ignore var ticker = Ticker()

    override fun equals(other: Any?): Boolean {
        val coinInfo = other as CoinInfo
        return (exchange == coinInfo.exchange && coinName == coinInfo.coinName)
    }
}