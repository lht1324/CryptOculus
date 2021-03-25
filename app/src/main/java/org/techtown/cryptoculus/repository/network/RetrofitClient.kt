package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

object RetrofitClient {
    val coinone = "coinone"
    val bithumb = "bithumb"
    val upbit = "upbit"
    val huobi = "huobi"

    fun getData(exchange: String): ArrayList<CoinInfo> { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        val url = when(exchange) {
            coinone -> "https://api.coinone.co.kr/"
            bithumb -> "https://api.bithumb.com/"
            upbit -> "https://api.upbit.com/v1/"
            else -> "https://api-cloud.huobi.co.kr/"
        }
        val parser = DataParser()
        lateinit var coinInfos: ArrayList<CoinInfo>

        val builder = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        val call: retrofit2.Call<Any> =
                when (exchange) {
                    coinone ->
                        builder.getTickersCoinone("all")
                    bithumb ->
                        builder.getTickersBithumb()
                    upbit ->
                        builder.getTickersUpbit(getMarketsUpbit())
                    else ->
                        builder.getTickersHuobi()
                }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                coinInfos = parser.getParsedData(exchange, response.body().toString())
                // processData without parameters?
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })

        return coinInfos
    }

    private fun getMarketsUpbit(): String {
        val parser = DataParser()
        lateinit var markets: String

        val builder = Retrofit.Builder()
                .baseUrl("https://api.upbit.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        val call: retrofit2.Call<Any> = builder.getMarketsUpbit()

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                markets = parser.parseUpbitMarkets(response.body().toString())
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })

        return markets
    }

    /* private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
    }

    private val retrofitService: RetrofitService by lazy {
        retrofitClient
                .build()
                .create(RetrofitService::class.java)
    } */

    fun println(data: String) {
        Log.d("Parser", data)
    }
}