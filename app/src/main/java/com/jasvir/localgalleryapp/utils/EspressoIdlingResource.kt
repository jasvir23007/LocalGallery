package com.jasvir.localgalleryapp.utils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

/**
 * @author Jasvir
 */

//When the counter is greater than zero, the app is considered working.
//When the counter is zero, the app is considered idle.
//Basically, whenever the app starts doing some work, increment the counter. When that work finishes,
//decrement the counter. Therefore, CountingIdlingResource will only have a "count" of zero if there
//is no work being done. This is a singleton so that you can access this idling resource anywhere
//in the app where long-running work might be done.

/**
 * Contains a static reference to [IdlingResource]
 *
 * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
 * are not scheduled in the main Looper (for example when executed on a different thread).
 */
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso does not work well with coroutines yet. See
    // https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}