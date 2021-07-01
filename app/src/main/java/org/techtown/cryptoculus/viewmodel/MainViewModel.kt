package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(val application: Application) : ViewModel(){
    private val compositeDisposable = CompositeDisposable()
    private val coinInfos = SingleLiveEvent<ArrayList<CoinInfo>>()
    private val downloadImageByName = MutableLiveData<String>()
    private val downloadEveryImage = SingleLiveEvent<Void>()
    private val updateKoreanByName = SingleLiveEvent<String>()
    private val updateKoreans = SingleLiveEvent<Void>()
    private var timer = Timer()
    private val repository by lazy {
        RepositoryImpl(application)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    fun getCoinInfos() = coinInfos

    fun getSortMode() = repository.getSortMode()

    fun getDownloadImageByName() = downloadImageByName

    fun getDownloadEveryImage() = downloadEveryImage

    fun getUpdateKoreanByName() = updateKoreanByName

    fun getUpdateKoreans() = updateKoreans

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                processExchangeData()
            }
        }, 0,3000)
    }

    override fun onCleared() {
        // 아이템이 클릭되었는지 여부를 나타내는 clicked 변수를 전부 false로 바꿔준다
        if (repository.getAllByExchange("Coinone").isNotEmpty())
            refreshClickedAll("Coinone")

        if (repository.getAllByExchange("Bithumb").isNotEmpty())
            refreshClickedAll("Bithumb")

        if (repository.getAllByExchange("Upbit").isNotEmpty())
            refreshClickedAll("Upbit")

        timer.cancel()
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun processExchangeData() = addDisposable(repository.getExchangeData()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map {
            CoinInfosMaker.maker(repository.getExchange(), it.body()!!)
        }
        .observeOn(Schedulers.io())
        .map {
            if (repository.getAll().isNotEmpty()) {
                val coinInfosOld = ArrayList(repository.getAll())

                if (coinInfosOld.size > it.size || coinInfosOld.size < it.size || (it.size == coinInfosOld.size && !it.containsAll(coinInfosOld) && !coinInfosOld.containsAll(it)))
                    updateModel(it)
            }
            else
                insertAll(it.toList())

            it
        }
        .map {
            if (getCoinNameKorean("BTC") == "" || getCoinNameKorean("BTC") == null) // insertAll()이 실행되었거나 실행되었지만 완료되지 않았을 때
                updateKoreans.postValue(null)
            else {
                for (i in it.indices)
                    it[i].coinNameKorean = getCoinNameKorean(it[i].coinName)!!
            }

            it
        }
        .toObservable()
        .observeOn(Schedulers.computation())
        .flatMap {
            Observable.fromIterable(it)
        }
        .observeOn(Schedulers.io())
        .filter {
            getCoinViewCheck(it.coinName)
        }
        .map {
            val coinNameKoreanTemp = getCoinNameKorean(it.coinName)

            if (coinNameKoreanTemp == null)
                updateKoreanByName.postValue(it.coinName)

            else {
                if (coinNameKoreanTemp == "신규 상장")
                    updateKoreanByName.postValue(it.coinName)

                it.coinNameKorean = coinNameKoreanTemp
            }

            it
        }
        .map {
            if (!repository.getFirstRun()) {
                if (it.coinName != "SHOW") {
                    val imageFileTemp = getImageFile(it.coinName)

                    if (imageFileTemp == null) { // 신규 상장인 경우
                        downloadImageByName.postValue(it.coinName)
                        it.image = "https://storage.googleapis.com/cryptoculus-58556.appspot.com/images/${
                            when (it.coinName) {
                                "1INCH" -> "inch"
                                "CON" -> "conun"
                                "TRUE" -> "truechain"
                                else -> it.coinName.lowercase()
                            }
                        }.png"
                    }
                    else { // 파일 다운로드를 시도한 적이 있을 때
                        val junkBitmapCheck = BitmapFactory.decodeFile(imageFileTemp.toString()).sameAs(Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888))
                        // true: junk, false: not junk

                        if (junkBitmapCheck) { // 이미지가 서버에 없어 다운로드 못 하고 있던 파일일 경우
                            if (it.coinNameKorean != "신규 상장") {
                                downloadImageByName.postValue(it.coinName)
                                it.image = "https://storage.googleapis.com/cryptoculus-58556.appspot.com/images/${
                                    when (it.coinName) {
                                        "1INCH" -> "inch"
                                        "CON" -> "conun"
                                        "TRUE" -> "truechain"
                                        else -> it.coinName.lowercase()
                                    }
                                }.png"
                            }
                            else
                                it.image = null
                        }
                        else
                            it.image = imageFileTemp.toString()
                    }
                }
            }

            it
        }
        .map {
            if (repository.getIdleCheck())
                it.clicked = repository.getClicked(it.coinName)

            it
        }
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                if (repository.getFirstRun() && repository.getAll().isNotEmpty()) {
                    downloadEveryImage.value = null
                    repository.putFirstRun(false)
                }

                if (!repository.getIdleCheck()) {
                    repository.refreshClickedAll(repository.getExchange())
                    changeIdleCheck()
                }

                coinInfos.value = ArrayList(it)
            },
            { println("onError in processExchangeData() of MainViewModel: ${it.message}")},
        )
    )

    private fun updateModel(coinInfosNew: ArrayList<CoinInfo>) {
        val coinInfosOld = ArrayList(repository.getAll())

        for (i in coinInfosNew.indices) {
            if (!coinInfosOld.contains(coinInfosNew[i])) {
                downloadImageByName.postValue(coinInfosNew[i].coinName)
                insert(coinInfosNew[i])
            }
        }

        for (i in coinInfosOld.indices) {
            if (!coinInfosNew.contains(coinInfosOld[i]))
                delete(coinInfosOld[i])
        }
    }

    private fun insert(coinInfo: CoinInfo) = completableTemplate(repository.insert(coinInfo))

    private fun insertAll(coinInfos: List<CoinInfo>) = completableTemplate(repository.insertAll(coinInfos))

    private fun delete(coinInfo: CoinInfo) = completableTemplate(repository.delete(coinInfo))

    fun updateClicked(coinName: String) = completableTemplate(repository.updateClicked(coinName))

    private fun getCoinViewCheck(coinName: String) = repository.getCoinViewCheck(coinName)

    fun changeExchange(position: Int) {
        repository.putExchange(when (position) {
            0 -> "Coinone"
            1 -> "Bithumb"
            else -> "Upbit" // 2
        })
        changeIdleCheck() // true to false
    }

    fun updateSortMode(sortMode: Int) {
        putSortMode(sortMode)
        changeIdleCheck() // true to false
    }

    fun getSelection() = when (repository.getExchange()) {
        "Coinone" -> 0
        "Bithumb" -> 1
        else -> 2 // "Upbit"
    }

    fun changeIdleCheck() {
        repository.putIdleCheck(!repository.getIdleCheck())

        if (!repository.getIdleCheck())
            processExchangeData()
    }

    private fun refreshClickedAll(exchange: String) = completableTemplate(repository.refreshClickedAll(exchange))

    private fun completableTemplate(argFun: Completable) = addDisposable(argFun
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe())

    private fun getCoinNameKorean(coinName: String): String? = repository.getCoinNameKorean(coinName)

    private fun getImageFile(coinName: String): File? = repository.getImageFile("${coinName.lowercase()}.png")

    private fun putSortMode(sortMode: Int) = repository.putSortMode(sortMode)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("MainViewModel", data)

    private fun println(tag: String, data: String) = Log.d(tag, data)
}