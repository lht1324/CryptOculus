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
    // ALL을 받아온 다음 키 값을 따서 저장한다. 첫 실행 시에는 즉석에서 파싱한다
    // 첫 실행 시 종료할 때는 키 값을 전부 저장한다
    // 재실행 시 저장된 키 값을 전부 불러온 뒤 다시 키 값을 따 오고, 비교한다
    // DB에 없으면 신규상장, DB에 있는데 ALL에 없으면 상장폐지
    // DB에 없으면 넣어준 다음 추가한다
    // Parse 부분에서 id 생성해줘야 할 거 같은데

    val coinone = "coinone"
    val bithumb = "bithumb"
    val upbit = "upbit"
    val huobi = "huobi"
    var url = ""

    fun getData(exchange: String): MutableLiveData<ArrayList<CoinInfo>> { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        val parser = DataParser()
        lateinit var coinInfos: ArrayList<CoinInfo>
        val call: retrofit2.Call<Any> =
                when (exchange) {
                    coinone ->
                        retrofitService.getTickersCoinone("all")
                    bithumb ->
                        retrofitService.getTickersBithumb()
                    upbit ->
                        retrofitService.getTickersUpbit(getMarketsUpbit())
                    else ->
                        retrofitService.getTickersHuobi()
                }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                coinInfos = parser.getParsedData(exchange, response.body().toString())
                // processData without parameters?
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                kotlin.io.println("Retrofit process is failed.")
            }
        })

        return MutableLiveData(coinInfos)
    }

    private fun getMarketsUpbit(): String {
        val parser = DataParser()
        val call: retrofit2.Call<Any> = retrofitService.getMarketsUpbit()
        lateinit var markets: String

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

    private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
    }

    private val retrofitService: RetrofitService by lazy {
        retrofitClient
                .build()
                .create(RetrofitService::class.java)
    }

    fun println(data: String) {
        Log.d("Parser", data)
    }
}