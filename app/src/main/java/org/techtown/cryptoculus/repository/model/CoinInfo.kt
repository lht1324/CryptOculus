package org.techtown.cryptoculus.repository.model

import androidx.room.Entity
import androidx.room.Ignore
import org.techtown.cryptoculus.pojo.Ticker

@Entity(tableName = "coinInfoTable", primaryKeys = ["exchange", "coinName"])
class CoinInfo {
    var exchange = "" // 거래소
    var coinNameOriginal = "" // 통신 시 받아오는 종목 코드 원본
    var coinName = "" // 가공 후 저장되는 종목 코드
    var coinViewCheck = true

    // 이건 새로 받아서 넣기만 하면 된다
    @Ignore var ticker = Ticker()
}