package com.jasvir.localgalleryapp.utils


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Jasvir
 *
 *  SingleLiveEvent is a lifecycle-aware observable that sends only new updates after subscription
 *  and it will call the observable only if there's an explicit call to setValue() or call().
 *  Note that only one observer is going to be notified of changes.
 */

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val TAG = "SingleLiveEvent"

    private val mPending = AtomicBoolean(false)

    // Snackbar
    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            value
        }
    }

    fun observe(owner: LifecycleOwner, f: (T?) -> Unit) {
        observe(owner, Observer {
            f(it)
        })
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(@Nullable t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    fun postCall() {
        postValue(null)
    }
}