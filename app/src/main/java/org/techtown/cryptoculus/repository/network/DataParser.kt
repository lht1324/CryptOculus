package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.json.JSONArray
import org.json.JSONObject
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.ticker.TickerBithumb
import org.techtown.cryptoculus.ticker.TickerCoinone
import org.techtown.cryptoculus.ticker.TickerHuobi
import org.techtown.cryptoculus.ticker.TickerUpbit
import org.techtown.cryptoculus.viewmodel.InitCoinInfos

class DataParser {
    val disposable: CompositeDisposable = CompositeDisposable()

    // Unit 넣어주는 걸로 대신하면 안 되나?
    // disposable 같은 걸로 Unit 넣은 다음에
    // coinInfos를 processData에 통지 받은 exchange랑 response 넣어주는 걸로 대신하는 거지

    fun getParsedData(exchange: String, response: String): ArrayList<CoinInfo> {
        return when (exchange) {
            "coinone" -> parseCoinone(exchange, response)
            "bithumb" -> parseBithumb(exchange, response)
            "upbit" -> parseUpbitTickers(exchange, response)
            else -> parseHuobi(exchange, response)
        }
    }

    private fun parseCoinone(exchange: String, response: String): ArrayList<CoinInfo> {
        // response 정상
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val coinNames = jsonObject.keys().asSequence().toList() as ArrayList<String>
        var coinInfos = ArrayList<CoinInfo>()

        for (i in 0..2)
            coinNames.removeAt(0)

        for (i in coinNames.indices) {
            val coinInfo = CoinInfo()
            coinInfo.ticker = gson.fromJson(jsonObject.get(coinNames[i]).toString(), TickerCoinone::class.java)
            coinInfo.coinNameOriginal = coinNames[i]
            coinInfos.add(coinInfo)
            // tickersCoinone.add(gson.fromJson(jsonObject.get(coinNames[i]).toString(), TickerCoinone::class.java))
        }

        coinInfos = InitCoinInfos.setTicker(exchange, coinInfos)!!
        return coinInfos
    }

    private fun parseBithumb(exchange: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val data = jsonObject.getJSONObject("data")
        val coinNames = data.keys().asSequence().toList() as ArrayList<String>
        val coinInfos = ArrayList<CoinInfo>()

        coinNames.removeAt(0)
        coinNames.removeAt(coinNames.size - 1)

        for (i in coinNames.indices) {
            val coinInfo = CoinInfo()
            coinInfo.ticker = gson.fromJson(data.get(coinNames[i]).toString(), TickerBithumb::class.java)
            coinInfo.coinNameOriginal = coinNames[i]
            coinInfos.add(coinInfo)
            // tickersBithumb.add(gson.fromJson(data.get(coinNames[i]).toString(), TickerBithumb::class.java))
        }

        return coinInfos
    }

    fun parseUpbitMarkets(response: String): String {
        val jsonArray = JSONArray(response)
        var markets = ""

        for (i in 0..jsonArray.length()) {
            if (jsonArray.getJSONObject(i).toString().contains("KRW")) {
                markets +=
                        if (i != jsonArray.length() - 1) "${jsonArray.getJSONObject(i).getString("market")},"
                        else jsonArray.getJSONObject(i).getString("market")
            }
        }

        return markets
    }

    private fun parseUpbitTickers(markets: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        val jsonArray = JSONArray(response)

        val coinNames = markets.split(",") as ArrayList<String>
        val coinInfos = ArrayList<CoinInfo>()

        for (i in 0..jsonArray.length()) {
            val coinInfo = CoinInfo()
            coinInfo.coinNameOriginal = coinNames[i]

            coinInfo.ticker = gson.fromJson(jsonArray[i].toString(), TickerUpbit::class.java)
            coinInfos.add(coinInfo)
            // tickersUpbit.add(gson.fromJson(jsonArray[i].toString(), TickerUpbit::class.java))
        }

        return coinInfos
    }

    private fun parseHuobi(exchange: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val data = jsonObject.getJSONArray("data")

        val coinInfos = ArrayList<CoinInfo>()

        for (i in 0..data.length()) {
            if (data.getJSONObject(i).toString().contains("krw")) {
                val coinInfo = CoinInfo()
                coinInfo.coinNameOriginal = data.getJSONObject(i).getString("symbol")
                coinInfo.ticker = gson.fromJson(data.getJSONObject(i).toString(), TickerHuobi::class.java)
                coinInfos.add(coinInfo)
                // tickersHuobi.add(gson.fromJson(data.getString(i), TickerHuobi::class.java))
            }
        }

        return coinInfos
    }

    private fun println(data: String) {
        Log.d("DataParser", data)
    }
}