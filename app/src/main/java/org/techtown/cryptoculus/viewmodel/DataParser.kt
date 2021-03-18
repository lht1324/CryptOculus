package org.techtown.cryptoculus.viewmodel

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.ticker.TickerBithumb
import org.techtown.cryptoculus.ticker.TickerCoinone
import org.techtown.cryptoculus.ticker.TickerHuobi
import org.techtown.cryptoculus.ticker.TickerUpbit

class DataParser {
    val coinInfos = ArrayList<CoinInfo>()

    private fun parseCoinone(response: String) {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val coinNames = jsonObject.keys().asSequence().toList() as ArrayList<String>

        for (i in 0..2)
            coinNames.removeAt(0)

        for (i in coinNames.indices) {
            val coinInfo = CoinInfo()
            coinInfo.ticker = gson.fromJson(jsonObject.get(coinNames[i]).toString(), TickerCoinone::class.java)
            coinInfo.coinNameOriginal = coinNames[i]
            coinInfos.add(coinInfo)
            // tickersCoinone.add(gson.fromJson(jsonObject.get(coinNames[i]).toString(), TickerCoinone::class.java))
        }
    }

    private fun parseBithumb(response: String) {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val data = jsonObject.getJSONObject("data")
        val coinNames = data.keys().asSequence().toList() as ArrayList<String>

        coinNames.removeAt(0)
        coinNames.removeAt(coinNames.size - 1)

        for (i in coinNames.indices) {
            val coinInfo = CoinInfo()
            coinInfo.ticker = gson.fromJson(data.get(coinNames[i]).toString(), TickerBithumb::class.java)
            coinInfo.coinNameOriginal = coinNames[i]
            coinInfos.add(coinInfo)
            // tickersBithumb.add(gson.fromJson(data.get(coinNames[i]).toString(), TickerBithumb::class.java))
        }
    }

    private fun parseUpbitMarkets(response: String) {
        val jsonArray = JSONArray(response)
        var markets = ""

        for (i in 0..jsonArray.length()) {
            if (jsonArray.getJSONObject(i).toString().contains("KRW")) {
                markets +=
                        if (i != jsonArray.length() - 1) "${jsonArray.getJSONObject(i).getString("market")},"
                        else jsonArray.getJSONObject(i).getString("market")
            }
        }

        getResponse(markets)
    }

    private fun parseUpbitTickers(response: String, markets: String) {
        val gson = Gson()
        val jsonArray = JSONArray(response)

        val coinNames = markets.split(",") as ArrayList<String>

        for (i in 0..jsonArray.length()) {
            val coinInfo = CoinInfo()
            coinInfo.coinNameOriginal = coinNames[i]

            coinInfo.ticker = gson.fromJson(jsonArray[i].toString(), TickerUpbit::class.java)
            coinInfos.add(coinInfo)
            // tickersUpbit.add(gson.fromJson(jsonArray[i].toString(), TickerUpbit::class.java))
        }
    }

    private fun parseHuobi(response: String) {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val data = jsonObject.getJSONArray("data")

        for (i in 0..data.length()) {
            if (data.getJSONObject(i).toString().contains("krw")) {
                val coinInfo = CoinInfo()
                coinInfo.coinNameOriginal = data.getJSONObject(i).getString("symbol")
                coinInfo.ticker = gson.fromJson(data.getJSONObject(i).toString(), TickerHuobi::class.java)
                coinInfos.add(coinInfo)
                // tickersHuobi.add(gson.fromJson(data.getString(i), TickerHuobi::class.java))
            }
        }
    }
}