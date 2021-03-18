package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.subjects.PublishSubject
import org.techtown.cryptoculus.repository.model.CoinDao
import org.techtown.cryptoculus.repository.model.CoinDatabase
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.Client

class ViewModel(application: Application) : ViewModel(){
    // id는 어쩌지?
    // 거래소마다 종목이 다른 건데
    // 각각 다르게 해야 하나?
    // exchange도 저장해야 할 거 같은데
    // 기존에 보고 있던 걸 보여주는 게 낫잖아
    // !restartApp -> insert(), restartApp -> update()
    private val client = Client()
    private val dataParser = DataParser()
    var coinInfos = ArrayList<CoinInfo>()
    val publishSubject: PublishSubject<String> = PublishSubject.create()
    val coinDao: CoinDao = CoinDatabase.getInstance(application)!!.coinDao()
    var restartApp = false
    var exchange = "coinone"
    // client.getData()를 getResponse()로 바꿔버릴까?
    // 그걸 dataParser에 넘겨주는 거야
    // dataParser.parseUpbit(
    //     client.getUpbitTickers(
    //         dataParser.parseUpbitMarkets(
    //             client.getUpbitMarkets()
    //         )
    //     )
    // )
    // 대충 이런 느낌이겠지
    // Respository { dataParser, client }
    // 이게 맞는 거 아닐까

    init {
        publishSubject.subscribe { exchange ->
            liveCoinInfos.value = client.getData(exchange)

            // 연동을 해버릴까?
            // 다른 rx랑 말이야
            // 여기서 exchange를 그 rx에 onNext로 주고 나서
            // 통지 들어오면 바꾸는 식으로
        }.dispose()
    }

    // 이 경우에 통지하는 건 MainActivity지
    // 옵저버가 여기서 받아야 하는 거고
    // onNext가 실행되면 observer는 그걸 받아서 가공하는 거지
    // ???

    // val coinInfos = ArrayList<CoinInfo>()
    val liveCoinInfos = MutableLiveData<ArrayList<CoinInfo>>() // MainActivity에서 observe 후 변경되면 어댑터 재설정

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T{
            return ViewModel(application) as T
        }
    }

    fun onCreate() {
        coinInfos =
                if (coinDao.getAll().isNotEmpty())
                    coinDao.getAll() as ArrayList<CoinInfo>
                else {
                    client.getData("coinone")
                }
        // 이러면 안 되지
        // 이건 비교할 때만 써야 하는 거야
        // getData로 받은 response 넣을 때 같이 넣는 걸로 하자
    }

    fun getData() {
        // 1. execute client
        // 2. parse data from client
        // coinone
        // getResponse() -> parseCoinone()
        // bithumb
        // getResponse() -> parseBithumb()
        // upbit
        // getResponse() -> parseUpbitMarkets() -> getResponse() -> parseUpbitTickers()
        // huobi
        // getResponse() -> parseHuobi
        when (exchange) {
            "coinone" -> {
                client.getData(exchange)
            }
            "bithumb" -> {

            }
            "upbit" -> {
                /*
                dataParser.parseUpbit(
                        client.getUpbitTickers(
                                dataParser.parseUpbitMarkets(
                                        client.getUpbitMarkets()
                                )
                        )
                )
                */
            }
            "huobi" -> {

            }
            else -> {

            }
        }
    }

    fun updateCoinInfos(coinInfos: ArrayList<CoinInfo>) {
        this.coinInfos = coinInfos
        liveCoinInfos.value = coinInfos
    }

    fun println(data: String) {
        Log.d("ViewModel", data)
    }

    /*
    val source = Observable.create<String> { it->
        it.onNext("Hello RxAndroid World")
        it.onComplete()
    }
    source.subscribe(observer)

    var observer = object:DisposableObserver<String>() {
        override fun onComplete() {
            Log.d("TEST","observer onComplete")
        }
        override fun onNext(t: String) {
            main_text.text = t
        }
        override fun onError(e: Throwable) {
        }
    }
    // Observable에서 onNext가 실행될 때마다, 즉 데이터가 통지될 때마다
    // 텍스트를 통지된 데이터로 바꾼다

    */
}