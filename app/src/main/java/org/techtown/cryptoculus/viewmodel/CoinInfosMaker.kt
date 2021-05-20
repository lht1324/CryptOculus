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
    val formatterOthers by lazy {
        DecimalFormat("###,##0.00")
    }
    lateinit var formatterPrice: DecimalFormat

    fun maker(exchange: String, response: Any): ArrayList<CoinInfo> {
        val coinInfos = ArrayList<CoinInfo>()

        when (exchange) {
            "Coinone" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString())
                val coinNameOriginals = ArrayList<String>(jsonObject.keys().asSequence().toList())

                for (i in 0..2)
                    coinNameOriginals.removeAt(0)

                for (i in coinNameOriginals.indices) {
                    coinInfos.add(CoinInfo().apply {
                        this.exchange = exchange
                        coinName = coinNameOriginals[i].toUpperCase()
                        ticker = tickerFormatter(gson.fromJson(jsonObject.get(coinNameOriginals[i]).toString(), TickerCoinone::class.java))
                    })
                }
            }
            "Bithumb" -> {
                val gson = Gson()
                val jsonObject = JSONObject(response.toString().replace("TRUE", "TRUETEMP")).getJSONObject("data") // '=TRUE'를 Boolean으로 인식한다
                val coinNameOriginals = ArrayList<String>(jsonObject.keys().asSequence().toList())

                coinNameOriginals.removeAt(coinNameOriginals.size - 1)

                for (i in coinNameOriginals.indices) {
                    coinInfos.add(CoinInfo().apply {
                        this.exchange = exchange
                        coinName = if (coinNameOriginals[i] != "TRUETEMP") coinNameOriginals[i] else "TRUE"
                        ticker = tickerFormatter(gson.fromJson(jsonObject.get(coinNameOriginals[i]).toString(), TickerBithumb::class.java))
                    })
                }
            }
            "Upbit" -> {
                val tickers = response as ArrayList<Ticker>

                for (i in tickers.indices) {
                    coinInfos.add(CoinInfo().apply {
                        this.exchange = exchange
                        coinName = (tickers[i] as TickerUpbit).market.replace("KRW-", "")
                        ticker = tickerFormatter(tickers[i])
                    })
                }
            }
        }

        return coinInfos
    }

    private fun tickerFormatter(ticker: Ticker): Ticker {
        formatterPrice = if (ticker.close.toDouble() - ticker.close.toDouble().roundToInt() != 0.0)
            DecimalFormat("###,##0.00")
        else
            DecimalFormat("###,###")

        return ticker.apply {
            changeRate = formatterOthers.format((ticker.close.toDouble() - ticker.yesterdayClose.toDouble()) / ticker.yesterdayClose.toDouble() * 100.0)
            open = formatterPrice.format(ticker.open.toDouble())
            close = formatterPrice.format(ticker.close.toDouble())
            max = formatterPrice.format(ticker.max.toDouble())
            min = formatterPrice.format(ticker.min.toDouble())
            tradeVolume = formatterOthers.format(ticker.tradeVolume.toDouble())
        }
    }

    private fun println(data: String) = Log.d("CoinInfosMaker", data)
}