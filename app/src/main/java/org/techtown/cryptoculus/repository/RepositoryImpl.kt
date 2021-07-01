package org.techtown.cryptoculus.repository

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.techtown.cryptoculus.repository.model.*
import org.techtown.cryptoculus.repository.network.Client
import java.io.File

class RepositoryImpl(private val application: Application) : Repository{
    private val client by lazy {
        Client()
    }
    private val imageFileHandler by lazy {
        ImageFileHandler(application.cacheDir)
    }
    private val coinInfoDao: CoinInfoDao by lazy {
        CoinInfoDatabase.getInstance(application)!!.coinInfoDao()
    }
    private val preferences by lazy {
        SavedSharedPreferencesImpl(application.getSharedPreferences("preferences", 0)) // MODE_PRIVATE
    }

    override fun getExchangeData() = client.getExchangeData(getExchange())

    override fun getImage(fileName: String) = client.getImage(Glide.with(application), fileName)

    override fun getAllByExchange(exchange: String) = coinInfoDao.getAllByExchange(exchange)

    override fun getAllAsSingle() = coinInfoDao.getAllByExchangeAsSingle(getExchange())

    override fun getAll() = coinInfoDao.getAllByExchange(getExchange())

    override fun getCoinInfo(coinName: String) = coinInfoDao.getCoinInfo(getExchange(), coinName)

    override fun getCoinNameKorean(coinName: String): String? = coinInfoDao.getCoinNameKorean(getExchange(), coinName)

    override fun getCoinViewCheck(coinName: String) = coinInfoDao.getCoinViewCheck(getExchange(), coinName)

    override fun getCoinViewChecks() = coinInfoDao.getCoinViewChecks(getExchange())

    override fun getClicked(coinName: String) = coinInfoDao.getClicked(getExchange(), coinName)

    override fun insert(coinInfo: CoinInfo) = coinInfoDao.insert(coinInfo)

    override fun insertAll(coinInfos: List<CoinInfo>) = coinInfoDao.insertAll(coinInfos)

    override fun updateCoinNameKorean(coinNameKorean: String, coinName: String) = coinInfoDao.updateCoinNameKorean(coinNameKorean, getExchange(), coinName)

    override fun updateCoinViewCheck(coinName: String) = coinInfoDao.updateCoinViewCheck(!coinInfoDao.getCoinViewCheck(getExchange(), coinName), getExchange(), coinName)

    override fun updateCoinViewCheckAll(checkAll: Boolean) = coinInfoDao.updateCoinViewCheckAll(checkAll, getExchange())

    override fun updateClicked(coinName: String) = coinInfoDao.updateClicked(!coinInfoDao.getClicked(getExchange(), coinName), getExchange(), coinName)

    override fun refreshClickedAll(exchange: String) = coinInfoDao.refreshClickedAll(exchange)

    override fun delete(coinInfo: CoinInfo) = coinInfoDao.delete(coinInfo)

    override fun getExchange() = preferences.getExchange()

    override fun putExchange(exchange: String) = preferences.putExchange(exchange)

    override fun getSortMode() = preferences.getSortMode()

    override fun putSortMode(sortMode: Int) = preferences.putSortMode(sortMode)

    override fun getIdleCheck() = preferences.getIdleCheck()

    override fun putIdleCheck(idleCheck: Boolean) = preferences.putIdleCheck(idleCheck)

    override fun getFirstRun() = preferences.getFirstRun()

    override fun putFirstRun(firstRun: Boolean) = preferences.putFirstRun(firstRun)

    override fun saveImageFile(bitmap: Bitmap, fileName: String) = imageFileHandler.saveImageFile(bitmap, fileName)

    override fun getImageFile(fileName: String): File? = imageFileHandler.getImageFile(fileName)

    override fun getCoinNameKoreansFirestore() = Firebase.firestore
        .collection("coinNameKoreans")
        .document("sODwDG1Mwi1RMOxRSUPy")
        .get()

    fun println(data: String) = Log.d("Repository", data)
}