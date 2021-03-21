package org.techtown.cryptoculus.repository.network

import android.util.Log
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.ticker.TickerBithumb
import org.techtown.cryptoculus.ticker.TickerCoinone
import org.techtown.cryptoculus.ticker.TickerHuobi
import org.techtown.cryptoculus.ticker.TickerUpbit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    // ALL을 받아온 다음 키 값을 따서 저장한다. 첫 실행 시에는 즉석에서 파싱한다
    // 첫 실행 시 종료할 때는 키 값을 전부 저장한다
    // 재실행 시 저장된 키 값을 전부 불러온 뒤 다시 키 값을 따 오고, 비교한다
    // DB에 없으면 신규상장, DB에 있는데 ALL에 없으면 상장폐지
    // DB에 없으면 넣어준 다음 추가한다
    // Parse 부분에서 id 생성해줘야 할 거 같은데

    val coinoneUrl = "https://api.coinone.co.kr/"
    val bithumbUrl = "https://api.bithumb.co.kr/"
    val huobiUrl = "https://api-cloud.huobi.co.kr/"
    val upbitUrl = "https://api.upbit.com/v1/"
    var url = ""

    val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
    }
    lateinit var coinInfos: ArrayList<CoinInfo>

    fun getData(exchange: String): ArrayList<CoinInfo> {
        coinInfos = ArrayList()
        when (exchange) {
            "coinone" -> url = coinoneUrl
            "bithumb" -> url = bithumbUrl
            "huobi" -> url = huobiUrl
            "upbit" -> url = upbitUrl
            else -> println("Error is occured in when of getData()")
        }
        getResponse("")
        return coinInfos
    }

    private fun getResponse(value: String) {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit: Retrofit = builder.build()
        val client: RetrofitService = retrofit.create(RetrofitService::class.java)

        val call: retrofit2.Call<Any> =
            when (url) {
                coinoneUrl ->
                    client.getTickersCoinone("all")
                bithumbUrl ->
                    client.getTickersBithumb()
                upbitUrl -> {
                    if (value.isNotBlank()) client.getMarketsUpbit()
                    else client.getTickersUpbit(value)
                }
                huobiUrl ->
                    client.getTickersHuobi()
                else -> {
                    println("Connection failed in making call.")
                    return
                }
            }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                when (url) {
                    coinoneUrl ->
                        parseCoinone(response.body().toString())
                    bithumbUrl ->
                        parseBithumb(response.body().toString())
                    upbitUrl -> {
                        if (value.isBlank()) parseUpbitMarkets(response.body().toString())
                        else parseUpbitTickers(response.body().toString(), value)
                    }
                    huobiUrl ->
                        parseHuobi(response.body().toString())
                    else -> {
                        println("Connection failed in onResponse.")
                        return
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
    }

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

        var coinNames = markets.split(",") as ArrayList<String>

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

    // 이건 뷰모델에서 가공하는 게 맞는 것 같은데
    // 네트워크는 말 그대로 받아서 가공만 해주면 되잖아

    fun println(data: String) {
        Log.d("Parser", data)
    }
}