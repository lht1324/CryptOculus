package org.techtown.cryptoculus.repository.network

import android.util.Log
import com.google.gson.Gson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.json.JSONArray
import org.json.JSONObject
import org.techtown.cryptoculus.pojo.*
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.viewmodel.InitCoinInfos

class DataParser() {
    val disposable: CompositeDisposable = CompositeDisposable()
    
    // 업비트만 따로 markets까지 넣어줘야 하는 거잖아
    // 일단 분리하거나 처리 가능한 방법을 알아보자

    fun getParsedData(exchange: String, response: String): ArrayList<CoinInfo> {
        return when (exchange) {
            "coinone" -> parseCoinone(exchange, response)
            "bithumb" -> parseBithumb(exchange, response)
            "huobi" -> parseHuobi(exchange, response)
            else -> parseUpbitTickers(exchange, response)
        }
    }

    private fun parseCoinone(exchange: String, response: String): ArrayList<CoinInfo> {
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
        }

        return InitCoinInfos.setTicker(exchange, coinInfos)!!
    }

    private fun parseBithumb(exchange: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        // "TRUE ="를 Boolean으로 인식한다...
        val data = JSONObject(response.replace("TRUE", "TRUETEMP")).getJSONObject("data")
        val coinNames = data.keys().asSequence().toList() as ArrayList<String>
        var coinInfos = ArrayList<CoinInfo>()

        coinNames.removeAt(coinNames.size - 1)

        for (i in coinNames.indices) {
            val coinInfo = CoinInfo()
            coinInfo.ticker = gson.fromJson(data.get(coinNames[i]).toString(), TickerBithumb::class.java)
            coinInfo.coinNameOriginal = if (coinNames[i] != "TRUETEMP") coinNames[i] else "TRUE"
            coinInfos.add(coinInfo)
        }

        return InitCoinInfos.setTicker(exchange, coinInfos)!!
    }

    fun parseUpbitMarkets(response: String): String {
        // "key" : "value"
        // 이 형태일 때 value에 공백이 포함되어 있다면 공백 다음의 2번째 글자에서 Unterminated 에러가 발생한다

        println("response = $response")

        var responseTemp = response.replace("}, {", "},{")
                .replace("{", "{\"") // { 오른쪽
                .replace("=", "\"=\"") // = 왼쪽 오른쪽
                .replace(", ", "\", \"") // ", " 왼쪽 오른쪽 (이 경우엔 }와 { 사이에 ", "가 있다면 제외)
                .replace("}", "\"}") // } 왼쪽
                .replace("},{", "}, {")
        println("responseTemp = $responseTemp")

        val jsonArray = JSONArray(responseTemp)
        var markets = ""

        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).getString("market").contains("KRW")) {
                markets +=
                        if (i != jsonArray.length() - 1)
                            "${jsonArray.getJSONObject(i).getString("market")},"
                        else
                            jsonArray.getJSONObject(i).getString("market")
            }
        }
        println("markets = $markets")

        return markets
    }

    private fun parseUpbitTickers(markets: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        val jsonArray = JSONArray(response)

        val coinNames = markets.split(",") as ArrayList<String>
        val coinInfos = ArrayList<CoinInfo>()

        for (i in 0 until jsonArray.length()) {
            val coinInfo = CoinInfo()
            coinInfo.coinNameOriginal = coinNames[i]

            coinInfo.ticker = gson.fromJson(jsonArray[i].toString(), TickerUpbit::class.java)
            coinInfos.add(coinInfo)
        }

        return InitCoinInfos.setTicker("upbit", coinInfos)!!
    }

    private fun parseHuobi(exchange: String, response: String): ArrayList<CoinInfo> {
        val gson = Gson()
        val jsonObject = JSONObject(response)
        val data = jsonObject.getJSONArray("data")

        val coinInfos = ArrayList<CoinInfo>()

        for (i in 0 until (data.length() - 1)) {
            if (data.getJSONObject(i).toString().contains("krw")) {
                val coinInfo = CoinInfo()
                coinInfo.coinNameOriginal = data.getJSONObject(i).getString("symbol")
                coinInfo.ticker = gson.fromJson(data.getJSONObject(i).toString(), TickerHuobi::class.java)
                coinInfos.add(coinInfo)
                // tickersHuobi.add(gson.fromJson(data.getString(i), TickerHuobi::class.java))
            }
        }

        return InitCoinInfos.setTicker(exchange, coinInfos)!!
    }

    private fun println(data: String) {
        Log.d("DataParser", data)
    }
}