package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.RepositoryImpl

class ImageViewModel(val application: Application) : ViewModel() {
    private val repository by lazy {
        RepositoryImpl(application)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ImageViewModel(application) as T
    }

    fun downloadImage(coinName: String) = getImage("${
        when (coinName) {
            "1INCH" -> "inch"
            "CON" -> "conun"
            "TRUE" -> "truechain"
            else -> coinName.toLowerCase()
        }}.png")
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) = saveImageFile(bitmap, coinName)
            override fun onLoadCleared(placeholder: Drawable?) { }
            override fun onLoadFailed(errorDrawable: Drawable?) {
                println("$coinName's onLoadFailed() is executed.")
                saveImageFile(Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888), coinName)
                super.onLoadFailed(errorDrawable)
            }
        })

    fun downloadEveryImage() = repository.getAllAsSingle()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map {
            ArrayList(it)
        }.toObservable()
        .flatMap {
            Observable.fromIterable(it)
        }
        .map {
            if (it.coinName != "SHOW") {
                    getImage("${when (it.coinName) {
                        "1INCH" -> "inch"
                        "CON" -> "conun"
                        "TRUE" -> "truechain"
                        else -> it.coinName.toLowerCase()
                    }
                    }.png").into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) = saveImageFile(bitmap, it.coinName)

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            println("${it.coinName}'s onLoadFailed() is executed.")
                            saveImageFile(Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888), it.coinName)
                            super.onLoadFailed(errorDrawable)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })
            }
            
            it
        }
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                println("ImageCheck() in ImageViewModel is finished.")
            },
            {
                println("onError in ImageViewModel: ${it.message}")
            }
        )
    
    private fun getImage(fileName: String) = repository.getImage(fileName)
    
    private fun saveImageFile(bitmap: Bitmap, coinName: String) = repository.saveImageFile(bitmap, "${coinName.toLowerCase()}.png")

    private fun println(data: String) = Log.d("ImageViewModel", data)
}