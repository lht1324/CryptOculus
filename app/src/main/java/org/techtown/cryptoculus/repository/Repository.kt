package org.techtown.cryptoculus.repository

import android.graphics.Bitmap
import com.bumptech.glide.RequestBuilder
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import io.reactivex.Completable
import io.reactivex.Single
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response
import java.io.File

interface Repository {
    fun getData(): Single<Response<Any>>

    fun getImage(fileName: String): RequestBuilder<Bitmap>

    fun getAllAsSingle(): Single<List<CoinInfo>>

    fun getAll(): List<CoinInfo>

    fun getAllByExchange(exchange: String): List<CoinInfo>

    fun getCoinInfo(coinName: String): CoinInfo

    fun getCoinNameKorean(coinName: String): String?

    fun getCoinViewCheck(coinName: String): Boolean

    fun getCoinViewChecks(): List<Boolean>

    fun getClicked(coinName: String): Boolean

    fun insert(coinInfo: CoinInfo): Completable

    fun insertAll(coinInfos: List<CoinInfo>): Completable

    fun updateCoinNameKorean(coinNameKorean: String, coinName: String): Completable

    fun updateCoinViewCheck(coinName: String): Completable

    fun updateCoinViewCheckAll(checkAll: Boolean): Completable

    fun updateClicked(coinName: String): Completable

    fun refreshClickedAll(exchange: String): Completable

    fun delete(coinInfo: CoinInfo): Completable

    fun getExchange(): String

    fun putExchange(exchange: String)

    fun getSortMode(): Int

    fun putSortMode(sortMode: Int)

    fun getIdleCheck(): Boolean

    fun putIdleCheck(idleCheck: Boolean)

    fun getFirstRun(): Boolean

    fun putFirstRun(firstRun: Boolean)

    fun saveImageFile(bitmap: Bitmap, fileName: String)

    fun getImageFile(fileName: String): File?

    fun getCoinNameKoreansFirestore(): Task<DocumentSnapshot>
}