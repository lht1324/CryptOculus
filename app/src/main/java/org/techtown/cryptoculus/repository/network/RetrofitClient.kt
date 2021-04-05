package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    interface MarketsCallback {
        fun onSuccess(marketsTemp: String)
    }

    // 일단 확실한 건 비동기 처리 때문에 getMarketsUpbit()가 getData()가 끝난 뒤에 실행된다는 거야
    // call에서 2번 실행해볼까?
    //
    val coinone = "coinone"
    val bithumb = "bithumb"
    val upbit = "upbit"
    val huobi = "huobi"
    val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    // val publishSubject: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()
    var markets = ""

    fun getData(exchange: String) { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        // var markets = "Basic"
        val url = when(exchange) {
            coinone -> "https://api.coinone.co.kr/"
            bithumb -> "https://api.bithumb.com/"
            upbit -> {
                val publishSubject: PublishSubject<String> = PublishSubject.create()
                publishSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            markets = it
                        }
                getMarketsUpbit(publishSubject)
                println("markets in getData() = $markets")
                // getResponse("https://api.upbit.com/v1/", "markets")
                "https://api.upbit.com/v1/"
            }
            else -> "https://api-cloud.huobi.co.kr/"
        }

        println("markets in getData() = $markets")
        getResponse(url, exchange)
    }

    private fun getResponse(url: String, exchange: String) {
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
                    builder.getTickersUpbit(markets)
                huobi ->
                    builder.getTickersHuobi()
                else ->
                    builder.getMarketsUpbit()
            }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                if (exchange == "markets") {
                    setMarkets(parser.parseUpbitMarkets(response.body().toString()))
                } else {
                    coinInfos.value = parser.getParsedData(
                            if (exchange != "upbit") exchange
                            else markets,
                            response.body().toString()
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
    }

    private fun getMarketsUpbit(publishSubject: PublishSubject<String>) {
        val parser = DataParser()
        var markets = ""

        val builder = Retrofit.Builder()
                .baseUrl("https://api.upbit.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        val call: retrofit2.Call<Any> = builder.getMarketsUpbit()

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                publishSubject.onNext(parser.parseUpbitMarkets(response.body().toString()))
            }

            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
    }

    @JvmName("setMarkets1")
    private fun setMarkets(markets: String) {
        this.markets = markets
    }

    @JvmName("getCoinInfos1")
    fun getCoinInfos() = coinInfos

    fun println(data: String) {
        Log.d("Parser", data)
    }
}