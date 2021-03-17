package org.techtown.cryptoculus.viewmodel

import android.util.Log
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.ticker.TickerBithumb
import org.techtown.cryptoculus.ticker.TickerCoinone
import org.techtown.cryptoculus.ticker.TickerHuobi
import org.techtown.cryptoculus.ticker.TickerUpbit
import java.text.DecimalFormat
import kotlin.math.roundToInt

class InitCoinInfos {
    fun setTicker(exchange: String, coinInfos: ArrayList<CoinInfo>): ArrayList<CoinInfo>? {
        for (coinInfo in coinInfos) {
            when (exchange) {
                "coinone" -> {
                    val ticker = coinInfo.ticker!! as TickerCoinone
                    val formatter = if (ticker.last.toDouble() - ticker.last.toDouble()
                            .roundToInt() != 0.0
                    ) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    coinInfo.ticker!!.firstInTicker = formatter.format(ticker.first.toDouble())
                    coinInfo.ticker!!.lastInTicker = formatter.format(ticker.last.toDouble())
                    coinInfo.ticker!!.highInTicker = formatter.format(ticker.high.toDouble())
                    coinInfo.ticker!!.lowInTicker = formatter.format(ticker.low.toDouble())
                    coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.volume.toDouble())
                    // coinInfo.ticker.tradeValueInTicker = formatter.format(ticker.)
                    // 그냥 거래량 같은 걸 추가하는 게 낫지 않아?
                }
                "bithumb" -> {
                    val ticker = coinInfo.ticker!! as TickerBithumb
                    val formatter =
                        if (ticker.closingPrice.toDouble() - ticker.closingPrice.toDouble()
                                .roundToInt() != 0.0
                        ) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    coinInfo.ticker!!.firstInTicker =
                        formatter.format(ticker.openingPrice.toDouble())
                    coinInfo.ticker!!.lastInTicker =
                        formatter.format(ticker.closingPrice.toDouble())
                    coinInfo.ticker!!.highInTicker = formatter.format(ticker.maxPrice.toDouble())
                    coinInfo.ticker!!.lowInTicker = formatter.format(ticker.minPrice.toDouble())
                    coinInfo.ticker!!.volumeInTicker =
                        formatter.format(ticker.unitsTraded.toDouble())
                }
                "upbit" -> {
                    val ticker = coinInfo.ticker!! as TickerUpbit
                    val formatter = if (ticker.tradePrice.toDouble() - ticker.tradePrice.toDouble()
                            .roundToInt() != 0.0
                    ) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    coinInfo.ticker!!.firstInTicker =
                        formatter.format(ticker.openingPrice.toDouble())
                    coinInfo.ticker!!.lastInTicker = formatter.format(ticker.tradePrice.toDouble())
                    coinInfo.ticker!!.highInTicker = formatter.format(ticker.highPrice.toDouble())
                    coinInfo.ticker!!.lowInTicker = formatter.format(ticker.lowPrice.toDouble())
                    coinInfo.ticker!!.volumeInTicker =
                        formatter.format(ticker.tradeVolume.toDouble())
                }
                "huobi" -> {
                    val ticker = coinInfo.ticker!! as TickerHuobi
                    val formatter = if (ticker.close.toDouble() - ticker.close.toDouble()
                            .roundToInt() != 0.0
                    ) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    coinInfo.ticker!!.firstInTicker = formatter.format(ticker.open.toDouble())
                    coinInfo.ticker!!.lastInTicker = formatter.format(ticker.close.toDouble())
                    coinInfo.ticker!!.highInTicker = formatter.format(ticker.high.toDouble())
                    coinInfo.ticker!!.lowInTicker = formatter.format(ticker.low.toDouble())
                    coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.vol.toDouble())
                }
                else -> {
                    println("exchange is wrong.")
                    return null // NPE로 종료
                }
            }
        }

        return coinInfos
    }

    private fun println(data: String) {
        Log.d("InitCoinInfos", data)
    }
}