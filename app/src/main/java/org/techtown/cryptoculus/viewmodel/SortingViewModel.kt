package org.techtown.cryptoculus.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class SortingViewModel(application: Application) : ViewModel() {
    private val repository by lazy {
        RepositoryImpl(application)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SortingViewModel(application) as T
        }
    }

    fun sortCoinInfos(coinInfos: ArrayList<CoinInfo>): ArrayList<CoinInfo> {
        coinInfos.sortWith { o1, o2 ->
            when (repository.getSortMode()) {
                0 -> o1.coinName.compareTo(o2.coinName) // 이름 오름차순
                1 -> o2.coinName.compareTo(o1.coinName) // 이름 내림차순
                2 -> o1.ticker.close.replace(",", "").toDouble().compareTo(o2.ticker.close.replace(",", "").toDouble()) // 현재가 오름차순
                3 -> o2.ticker.close.replace(",", "").toDouble().compareTo(o1.ticker.close.replace(",", "").toDouble()) // 현재가 내림차순
                4 -> o1.ticker.changeRate.toDouble().compareTo(o2.ticker.changeRate.toDouble()) // 등락률 오름차순
                else -> o2.ticker.changeRate.toDouble().compareTo(o1.ticker.changeRate.toDouble()) // 등락률 내림차순
            }
        }
        return coinInfos
    }
}