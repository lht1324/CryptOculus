package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.RepositoryImpl

class FirestoreViewModel(application: Application) : ViewModel() {
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    private val repository by lazy {
        RepositoryImpl(application)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = FirestoreViewModel(application) as T
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getCoinNameKoreansFirestore() = repository.getCoinNameKoreansFirestore()
        .addOnSuccessListener { hashMap ->
            val coinInfos = repository.getAll()

            for (i in coinInfos.indices) {
                if (hashMap[coinInfos[i].coinName] != null) {
                    val coinNameKorean = hashMap[coinInfos[i].coinName] as String

                    updateCoinNameKorean(coinNameKorean, coinInfos[i].coinName)
                }
                else
                    updateCoinNameKorean("신규 상장", coinInfos[i].coinName)}
        }
        .addOnFailureListener {
            println("onFailure in getCoinNameKoreansFirestore(): ${it.message}")
        }

    fun getCoinNameKoreanFirestore(coinName: String) = repository.getCoinNameKoreansFirestore()
        .addOnSuccessListener { hashMap ->
            if (hashMap[coinName] != null)
                updateCoinNameKorean(hashMap[coinName] as String, coinName)
        }
        .addOnFailureListener { it ->
            println("onFailure in getCoinNameKoreanFirestore($coinName): ${it.message}")
        }

    private fun updateCoinNameKorean(coinNameKorean: String, coinName: String) = addDisposable(repository.updateCoinNameKorean(coinNameKorean, coinName)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe())

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("FirestoreViewModel", data)
}