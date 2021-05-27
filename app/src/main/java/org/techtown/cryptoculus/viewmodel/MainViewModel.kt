package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.pojo.Ticker
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(application: Application) : ViewModel(){
    private val compositeDisposable = CompositeDisposable()
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
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

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                getData()
            }
        }, 0,3000)
    }

    override fun onCleared() {
        if (repository.getAllByExchange("Coinone").isNotEmpty())
            repository.refreshClickedAll("Coinone")

        if (repository.getAllByExchange("Bithumb").isNotEmpty())
            repository.refreshClickedAll("Bithumb")

        if (repository.getAllByExchange("Upbit").isNotEmpty())
            repository.refreshClickedAll("Upbit")

        timer.cancel()
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getData() = addDisposable(repository.getData()
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
                    updateDB(it)
            }
            else
                insertAll(it.toList())

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
            it.clicked = if (repository.getIdleCheck()) repository.getClicked(it.coinName) else false
            it
        }.toList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                if (!repository.getIdleCheck()) {
                    repository.refreshClickedAll(repository.getExchange())
                    changeIdleCheck()
                }

                coinInfos.value = ArrayList(it)
            },
            { println("onError: ${it.message}")}
        )
    )

    private fun updateDB(coinInfosNew: ArrayList<CoinInfo>) {
        val coinInfosOld = ArrayList(repository.getAll())

        for (i in coinInfosNew.indices) {
            if (!coinInfosOld.contains(coinInfosNew[i]))
                insert(coinInfosNew[i])
        }

        for (i in coinInfosOld.indices) {
            if (!coinInfosNew.contains(coinInfosOld[i]))
                delete(coinInfosOld[i])
        }
    }

    private fun insert(coinInfo: CoinInfo) = repository.insert(coinInfo)

    private fun insertAll(coinInfos: List<CoinInfo>) = repository.insertAll(coinInfos)

    private fun delete(coinInfo: CoinInfo) = repository.delete(coinInfo)

    private fun getCoinViewCheck(coinName: String) = repository.getCoinViewCheck(coinName)

    fun updateClicked(coinName: String) = repository.updateClicked(coinName)

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
            getData()
    }

    private fun putSortMode(sortMode: Int) = repository.putSortMode(sortMode)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("MainViewModel", data)
}