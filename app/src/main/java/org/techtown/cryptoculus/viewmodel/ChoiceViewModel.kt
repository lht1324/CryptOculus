package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class ChoiceViewModel(application: Application) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val checkAll = MutableLiveData<Boolean>()

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChoiceViewModel(application) as T
        }
    }

    init {
        processExchangeData()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    
    private fun processExchangeData() = addDisposable(repository.getAllAsSingle()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
            for (i in it.indices) {
                if (i < it.size - 1 && !it[i].coinViewCheck) {
                    checkAll.value = false
                    break
                }
                if (i == it.size - 1 && it[i].coinViewCheck)
                    checkAll.value = true
            }
            ArrayList(it)
        }.toObservable()
        .flatMap {
            Observable.fromIterable(it)
        }
        .map {
            if (it.coinName != "SHOW") {
                val imageFileTemp = getImageFile(it.coinName)

                println("${it.coinName}'s file = $imageFileTemp")
                if (imageFileTemp == null) {
                    it.image = "https://storage.googleapis.com/cryptoculus-58556.appspot.com/images/${
                        when (it.coinName) {
                            "1INCH" -> "inch"
                            "CON" -> "conun"
                            "TRUE" -> "truechain"
                            else -> it.coinName.lowercase()
                        }
                    }.png"
                }
                else {
                    val junkBitmapCheck = BitmapFactory.decodeFile(imageFileTemp.toString()).sameAs(
                        Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888))
                    // true: junk, false: not junk

                    if (junkBitmapCheck) {
                        if (it.coinNameKorean != "신규 상장") {
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

            it
        }
        .toList()
        .subscribe(
            {
                coinInfos.value = ArrayList(it)
            },
            { println("onError: ${it.message}") }
        )
    )

    fun getCoinInfos() = coinInfos

    fun getCheckAll() = checkAll

    // 전체 체크 눌렀을 때
    fun changeCoinViewCheckAll(coinInfos: ArrayList<CoinInfo>, checkAll: Boolean): ArrayList<CoinInfo> {
        coinInfos.replaceAll {
            it.coinViewCheck = !checkAll
            it
        }
        this.checkAll.value = !checkAll
        updateAll(!checkAll)

        return coinInfos
    }

    fun updateCoinViewCheck(coinName: String) = addDisposable(repository.updateCoinViewCheck(coinName)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                if (checkAll.value!!)
                    checkAll.value = false

                else {
                    val coinViewChecks = repository.getCoinViewChecks()

                    for (i in coinViewChecks.indices) {
                        if (i < coinViewChecks.size - 1 && !coinViewChecks[i])
                            break
                        if (i == coinViewChecks.size - 1 && coinViewChecks[i])
                            checkAll.value = true
                    }
                }
            },
            { println("onError: ${it.message}") }
        )
    )

    private fun updateAll(checkAll: Boolean) = addDisposable(repository.updateCoinViewCheckAll(checkAll)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe()
    )

    private fun getImageFile(coinName: String) = repository.getImageFile("${coinName.lowercase()}.png")

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("ChoiceViewModel", data)
}