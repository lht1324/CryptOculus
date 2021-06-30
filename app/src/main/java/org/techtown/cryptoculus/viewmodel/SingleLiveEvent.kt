package org.techtown.cryptoculus.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

@MainThread
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers())
            Log.d("SingleLiveObserver", "Multiple observers registered but only one will be notified of changes.")

        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false))
                observer.onChanged(t)
        })
    }

    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    fun call() {
        value = null
    }
}