package com.jasvir.localgalleryapp.util

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewFinder
import org.hamcrest.Matcher
import java.lang.reflect.Field
import java.util.*


/**
 * @author Jasvir
 *
 * @param viewMatcher The matcher to find the view.
 * @param idleMatcher The matcher condition to be fulfilled to be considered idle.
*/

class ViewIdlingResource(
    private val matcher: Matcher<View>,
    private val idleMatcher: Matcher<View?>?) : IdlingResource {

    // List of registered callbacks
    private var idlingCallback : IdlingResource.ResourceCallback? = null
    // Give it a unique id to work around an Espresso bug where you cannot register/unregister
    // an idling resource with the same name.
    private val id = UUID.randomUUID().toString()

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {

        val view: View? = getView(matcher)
        val isIdle: Boolean = idleMatcher?.matches(view) ?: false
        if (isIdle) {
            idlingCallback?.onTransitionToIdle()
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallback = callback
    }

    private fun getView(viewMatcher: Matcher<View>): View? =
        try {
            val viewInteraction = onView(viewMatcher)
            val finderField: Field? = viewInteraction.javaClass.getDeclaredField("viewFinder")
            finderField?.isAccessible = true
            val finder = finderField?.get(viewInteraction) as ViewFinder
            finder.view
        } catch (e: Exception) {
            null
        }

}
