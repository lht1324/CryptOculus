package org.techtown.cryptoculus.viewmodel

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.techtown.cryptoculus.pojo.*
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response
import java.text.DecimalFormat
import kotlin.math.roundToInt

object CoinInfosMaker {
    // coinone -> "btc": toUpperCase()
    // bithumb -> "BTC"
    // upbit -> "KRW-BTC": "KRW-"를 삭제
    // huobi -> "krwbtc": "krw" 삭제 후 toUpperCase()
    fun maker(exchange: String, response: Any): ArrayList<CoinInfo> {
        val coinInfos = ArrayList<CoinInfo>()
        lateinit var formatterPrice: DecimalFormat
        lateinit var formatterVolume: DecimalFormat

        when (exchange) {
            "coinone" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString())
                val coinNameOriginals = jsonObject.keys().asSequence().toList() as ArrayList<String>

                for (i in 0..2)
                    coinNameOriginals.removeAt(0)

                for (i in coinNameOriginals.indices) {
                    val coinInfo = CoinInfo()
                    val tickerTemp = gson.fromJson(jsonObject.get(coinNameOriginals[i]).toString(), TickerCoinone::class.java)

                    formatterPrice = if (tickerTemp.last.toDouble() - tickerTemp.last.toDouble().roundToInt() != 0.0)
                                DecimalFormat("###,##0.00")
                            else
                                DecimalFormat("###,###")

                    formatterVolume = DecimalFormat("###,##0.00")

                    coinInfo.apply {
                        this.exchange = exchange
                        ticker.apply {
                            firstInTicker = formatterPrice.format(tickerTemp.first.toDouble())
                            lastInTicker = formatterPrice.format(tickerTemp.last.toDouble())
                            highInTicker = formatterPrice.format(tickerTemp.high.toDouble())
                            lowInTicker = formatterPrice.format(tickerTemp.low.toDouble())
                            volumeInTicker = formatterVolume.format(tickerTemp.volume.toDouble())
                        }
                        coinNameOriginal = coinNameOriginals[i]
                        coinName = coinNameOriginals[i].toUpperCase()
                    }
                    coinInfos.add(coinInfo)
                }
            }
            "bithumb" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString().replace("TRUE", "TRUETEMP")).getJSONObject("data")
                val coinNameOriginals = jsonObject.keys().asSequence().toList() as ArrayList<String>

                coinNameOriginals.removeAt(coinNameOriginals.size - 1)

                for (i in coinNameOriginals.indices) {
                    val coinInfo = CoinInfo()
                    coinInfo.apply {
                        this.exchange = exchange
                        val tickerTemp = gson.fromJson(jsonObject.get(coinNameOriginals[i]).toString(), TickerBithumb::class.java)

                        formatterPrice = if (tickerTemp.closingPrice.toDouble() - tickerTemp.closingPrice.toDouble().roundToInt() != 0.0)
                            DecimalFormat("###,##0.00")
                        else
                            DecimalFormat("###,###")

                        formatterVolume = DecimalFormat("###,##0.00")

                        ticker!!.apply {
                            firstInTicker = formatterPrice.format(tickerTemp.openingPrice.toDouble())
                            lastInTicker = formatterPrice.format(tickerTemp.closingPrice.toDouble())
                            highInTicker = formatterPrice.format(tickerTemp.maxPrice.toDouble())
                            lowInTicker = formatterPrice.format(tickerTemp.minPrice.toDouble())
                            volumeInTicker = formatterPrice.format(tickerTemp.unitsTraded.toDouble())
                        }

                        if (coinNameOriginals[i] == "TRUETEMP") {
                            coinNameOriginal = "TRUE"
                            coinName = "TRUE"
                        }
                        else {
                            coinNameOriginal = coinNameOriginals[i]
                            coinName = coinNameOriginals[i]
                        }
                    }
                    coinInfos.add(coinInfo)
                }
            }
            "upbit" -> { // ArrayList<TickerUpbit>
                val tickers = response as ArrayList<TickerUpbit>

                for (i in tickers.indices) {
                    val coinInfo = CoinInfo()

                    formatterPrice = if (tickers[i].tradePrice.toDouble() - tickers[i].tradePrice.toDouble().roundToInt() != 0.0)
                        DecimalFormat("###,##0.00")
                    else
                        DecimalFormat("###,###")

                    formatterVolume = DecimalFormat("###,##0.000")

                    coinInfo.apply {
                        this.exchange = exchange
                        ticker.apply {
                            firstInTicker = formatterPrice.format(tickers[i].openingPrice.toDouble())
                            lastInTicker = formatterPrice.format(tickers[i].tradePrice.toDouble())
                            highInTicker = formatterPrice.format(tickers[i].highPrice.toDouble())
                            lowInTicker = formatterPrice.format(tickers[i].lowPrice.toDouble())
                            volumeInTicker = formatterVolume.format(tickers[i].tradeVolume.toDouble())
                        }
                        coinNameOriginal = tickers[i].market
                        coinName = tickers[i].market.replace("KRW-", "")
                    }
                    coinInfos.add(coinInfo)
                }
            }
            "huobi" -> { // huobi
                val tickersTemp = (response as Huobi).data
                var tickers = ArrayList<TickerHuobi>()

                for (i in tickersTemp.indices)
                    if (tickersTemp[i].symbol.contains("krw"))
                        tickers.add(tickersTemp[i])

                for (i in tickers.indices) {
                    val coinInfo = CoinInfo()

                    formatterPrice = if (tickers[i].close.toDouble() - tickers[i].close.toDouble().roundToInt() != 0.0)
                        DecimalFormat("###,##0.00")
                    else
                        DecimalFormat("###,###")

                    formatterVolume = DecimalFormat("###,##0.000")

                    coinInfo.apply {
                        this.exchange = exchange
                        ticker.apply {
                            firstInTicker = formatterPrice.format(tickers[i].open.toDouble())
                            lastInTicker = formatterPrice.format(tickers[i].close.toDouble())
                            highInTicker = formatterPrice.format(tickers[i].high.toDouble())
                            lowInTicker = formatterPrice.format(tickers[i].low.toDouble())
                            volumeInTicker = formatterVolume.format(tickers[i].amount.toDouble())
                        }
                        coinNameOriginal = tickers[i].symbol
                        coinName = tickers[i].symbol.replace("krw", "").toUpperCase()
                    }
                    coinInfos.add(coinInfo)
                }
            }
        }

        for (i in coinInfos.indices)
            println("coinInfos[$i] of $exchange = ${coinInfos[i].coinName}")

        return coinInfos
    }

    private fun println(data: String) {
        Log.d("InitCoinInfos", data)
    }
}