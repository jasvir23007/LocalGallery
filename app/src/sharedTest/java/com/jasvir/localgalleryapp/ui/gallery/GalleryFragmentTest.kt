package com.jasvir.localgalleryapp.ui.gallery

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.jasvir.localgalleryapp.R
import com.jasvir.localgalleryapp.TestApp
import com.jasvir.localgalleryapp.data.models.Result
import com.jasvir.localgalleryapp.data.source.FakeRepository
import com.jasvir.localgalleryapp.data.source.PicGalleryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito

/**
 * @author Jasvir
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class GalleryFragmentTest : KoinTest{

    // Use a fake repository to be injected
    private lateinit var repository: PicGalleryRepository

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })

        // Fill the db
        runBlocking {

            val uri = Uri.parse("android.resource://com.example.picgalleryapp/drawable/test_image").toString()

            repository.savePicture(uri)
        }
    }

    // Camera permission
    @Before
    fun cameraPermissionInit(){

        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)

        for (i in 0 until permissions.size) {
            val command = java.lang.String.format(
                "pm grant %s %s",
                InstrumentationRegistry.getInstrumentation().targetContext.packageName,
                permissions[i]
            )
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
            // wait a bit until the command is finished
            SystemClock.sleep(1000)
        }
    }

    @Test
    fun checkImageOnStart(){
        // GIVEN - On the home screen
        launchFragment()

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

    }


    @Test
    fun deleteImage(){
        // GIVEN - On the home screen
        launchFragment()

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        onView(withText(R.string.delete_images)).perform(click())

        // Check if image exist
        val pics = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }
        Assert.assertEquals(pics.size, 0)
    }


    @Test
    fun addPhoto(){
        // GIVEN - On the home screen
        launchFragment()

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        onView(withText(R.string.take_photo)).perform(click())

        val pics = runBlocking {
            repository.savePicture("UriTest")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[0].uri, "android.resource://com.example.picgalleryapp/drawable/test_image")
        Assert.assertEquals(pics[1].uri, "UriTest")
    }

    @Test
    fun addFromRoll(){
        // GIVEN - On the home screen
        launchFragment()

        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        onView(withText(R.string.pick_image)).perform(click())

        val pics = runBlocking {
            repository.savePicture("UriTest")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[0].uri, "android.resource://com.example.picgalleryapp/drawable/test_image")
        Assert.assertEquals(pics[1].uri, "UriTest")
    }


    // https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
    private fun <T> customMatcherForDrawable(imageId: Int): Matcher<T> {

        return object : BaseMatcher<T>() {
            override fun matches(item: Any?): Boolean {
                if (item !is ImageView)
                    return false

                val bitmap1 = getBitmap(item.drawable)
                val bitmap2 = getBitmap(item.resources.getDrawable(imageId, null))

                return bitmap1.sameAs(bitmap2)
            }

            override fun describeTo(description: Description?) {}


            fun getBitmap(drawable: Drawable): Bitmap {
                val bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
                );
                val canvas = Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }

        }
    }

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<GalleryFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

}