package org.techtown.cryptoculus.function

import org.techtown.cryptoculus.repository.model.CoinInfo

interface DataTransferMain {
    fun changeRestartApp(restartApp: Boolean)
    fun changeCoinInfos(coinInfos: ArrayList<CoinInfo?>)
    fun isEmptyCoinone(): Boolean
    fun isEmptyBithumb(): Boolean
    fun isEmptyHuobi(): Boolean
}