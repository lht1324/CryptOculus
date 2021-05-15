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
    // Coinone -> "btc": toUpperCase()
    // Bithumb -> "BTC"
    // Upbit -> "KRW-BTC": "KRW-"를 삭제
    fun maker(exchange: String, response: Any): ArrayList<CoinInfo> {
        val coinInfos = ArrayList<CoinInfo>()
        lateinit var formatterPrice: DecimalFormat
        lateinit var formatterVolume: DecimalFormat
        lateinit var formatterChangeRate: DecimalFormat

        when (exchange) {
            "Coinone" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString())
                val coinNameOriginals = ArrayList<String>(jsonObject.keys().asSequence().toList())

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
                    formatterChangeRate = DecimalFormat("###,##0.00")

                    coinInfo.apply {
                        this.exchange = exchange
                        ticker.apply {
                            firstInTicker = formatterPrice.format(tickerTemp.first.toDouble())
                            lastInTicker = formatterPrice.format(tickerTemp.last.toDouble())
                            highInTicker = formatterPrice.format(tickerTemp.high.toDouble())
                            lowInTicker = formatterPrice.format(tickerTemp.low.toDouble())
                            volumeInTicker = formatterVolume.format(tickerTemp.volume.toDouble())
                            changeRate = formatterChangeRate.format((tickerTemp.last.toDouble() - tickerTemp.yesterdayLast.toDouble()) / tickerTemp.yesterdayLast.toDouble() * 100.0)
                        }
                        coinName = coinNameOriginals[i].toUpperCase()
                    }
                    coinInfos.add(coinInfo)
                }
            }

            "Bithumb" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString().replace("TRUE", "TRUETEMP")).getJSONObject("data")
                val coinNameOriginals = ArrayList<String>(jsonObject.keys().asSequence().toList())

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
                        formatterChangeRate = DecimalFormat("###,##0.00")

                        ticker!!.apply {
                            firstInTicker = formatterPrice.format(tickerTemp.openingPrice.toDouble())
                            lastInTicker = formatterPrice.format(tickerTemp.closingPrice.toDouble())
                            highInTicker = formatterPrice.format(tickerTemp.maxPrice.toDouble())
                            lowInTicker = formatterPrice.format(tickerTemp.minPrice.toDouble())
                            volumeInTicker = formatterVolume.format(tickerTemp.unitsTraded.toDouble())
                            changeRate = formatterChangeRate.format((tickerTemp.closingPrice.toDouble() - tickerTemp.prevClosingPrice.toDouble()) / tickerTemp.prevClosingPrice.toDouble() * 100.0)
                        }

                        coinName = if (coinNameOriginals[i] == "TRUETEMP")
                            "TRUE"
                        else
                            coinNameOriginals[i]
                    }
                    coinInfos.add(coinInfo)
                }
            }

            "Upbit" -> { // ArrayList<TickerUpbit>
                val tickers = response as ArrayList<TickerUpbit>

                for (i in tickers.indices) {
                    val coinInfo = CoinInfo()

                    formatterPrice = if (tickers[i].tradePrice.toDouble() - tickers[i].tradePrice.toDouble().roundToInt() != 0.0)
                        DecimalFormat("###,##0.00")
                    else
                        DecimalFormat("###,###")

                    formatterVolume = DecimalFormat("###,##0.000")
                    formatterChangeRate = DecimalFormat("###,##0.00")

                    coinInfo.apply {
                        this.exchange = exchange
                        ticker.apply {
                            firstInTicker = formatterPrice.format(tickers[i].openingPrice.toDouble())
                            lastInTicker = formatterPrice.format(tickers[i].tradePrice.toDouble())
                            highInTicker = formatterPrice.format(tickers[i].highPrice.toDouble())
                            lowInTicker = formatterPrice.format(tickers[i].lowPrice.toDouble())
                            volumeInTicker = formatterVolume.format(tickers[i].tradeVolume.toDouble())
                            changeRate = formatterChangeRate.format((tickers[i].tradePrice.toDouble() - tickers[i].prevClosingPrice.toDouble()) / tickers[i].prevClosingPrice.toDouble() * 100.0)
                        }
                        coinName = tickers[i].market.replace("KRW-", "")
                    }
                    coinInfos.add(coinInfo)
                }
            }
        }

        return coinInfos
    }

    private fun println(data: String) = Log.d("CoinInfosMaker", data)
}