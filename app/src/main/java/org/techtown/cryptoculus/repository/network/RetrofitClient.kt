package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.LiveData
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
    val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()

    // retrofit2 실행 시 순차적 실행이 되지 않는다
    // 파싱해서 나온 결과를 어떻게 보내야 하지?
    // 뷰모델 -> 리포지토리 -> 클라이언트잖아
    // 일단 실행을 하는 거야
    // 실행을 한 결과는 라이브데이터가 바뀌는 거지
    // 그 라이브데이터를 옵저버하는 거고
    // 굳이 getData로 출력을 할 필요가 없지
    // 리포지토리에서 관찰하다 getData()의 실행 결과로 데이터가 변경되면 그걸 가져오면 된다
    // 내가 버튼을 누르면 최종적으로 클라이언트의 getData가 실행만 되면 된다
    // 그걸 라이브데이터 연속 관찰로 보내주면 되는 거고
    fun getData(exchange: String) { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        val url = when(exchange) {
            coinone -> "https://api.coinone.co.kr/"
            bithumb -> "https://api.bithumb.com/"
            upbit -> "https://api.upbit.com/v1/"
            else -> "https://api-cloud.huobi.co.kr/"
        }
        val parser = DataParser()

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
                coinInfos.value = parser.getParsedData(exchange, response.body().toString())
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
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

    @JvmName("getCoinInfos1")
    fun getCoinInfos() = coinInfos

    fun println(data: String) {
        Log.d("Parser", data)
    }
}