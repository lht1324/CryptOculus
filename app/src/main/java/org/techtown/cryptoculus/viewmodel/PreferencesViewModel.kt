package org.techtown.cryptoculus.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class PreferencesViewModel(application: Application) : ViewModel() {
    /*
    무언가 변경하는 경우는
    옵션에서 coinViewCheck가 변경되었을 때,
     */
    /*
    DB를 LiveData를 뱉는 걸로 만들고 그게 업데이트되면 자동으로 observe하도록 한다
     */
    private val compositeDisposable = CompositeDisposable()
    private val repository by lazy {
        RepositoryImpl(application)
    }
    
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun update(coinInfo: CoinInfo) = repository.update(coinInfo)

    fun updateAll(coinInfos: ArrayList<CoinInfo>) = repository.updateAll(coinInfos)

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)
}