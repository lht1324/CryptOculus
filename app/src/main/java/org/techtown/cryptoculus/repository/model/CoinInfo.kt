package org.techtown.cryptoculus.repository.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.techtown.cryptoculus.ticker.Ticker

@Entity(tableName = "coinInfoTable")
class CoinInfo {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var coinName = ""
    var coinNameKorean = ""
    var coinViewCheck = true

    @Ignore
    var ticker: Ticker? = null
}