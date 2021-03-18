package org.techtown.cryptoculus.repository.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.techtown.cryptoculus.ticker.Ticker

@Entity(tableName = "coinInfoTable", primaryKeys = ["exchange", "coinName"])
class CoinInfo {
    /*
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 */

    var exchange = "" // 거래소
    var coinNameOriginal = "" // 통신 시 받아오는 종목 코드 원본
    var coinName = "" // 가공 후 저장되는 종목 코드
    var coinNameKorean = "" // 원본으로 받아오는 한글명
    var coinViewCheck = true

    @Ignore
    var ticker: Ticker? = null
}