package com.jasvir.localgalleryapp.ui.camera

import android.Manifest
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
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
import com.jasvir.localgalleryapp.util.ViewIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import com.google.common.truth.Truth.*

/**
 * @author Jasvir
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class CameraFragmentTest : KoinTest {

    // Use a fake repository to be injected
    private lateinit var repository: PicGalleryRepository

    private val viewModel: CameraViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })
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
    fun takePhoto() {

        // GIVEN - On the camera screen
        launchFragment()

        // Flag for camera set
        assertThat(viewModel.isCameraVisible.get(), `is`(true))

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentRotateButton)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        onView(withId(R.id.cameraFragmentRotateButton)).check(matches(isDisplayed()))
    }

    @Test
    fun saveTakenPhoto() {

        // GIVEN - On the camera screen
        launchFragment()

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        // Take photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        val pics = runBlocking {
            repository.savePicture("UriTest")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[1].uri, "UriTest")

    }

    @Test
    fun saveTakenRotatePhoto() {

        // GIVEN - On the camera screen
        launchFragment()

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        // Take photo
        onView(withId(R.id.cameraFragmentRotateButton)).perform(click())

        // Take photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        val pics = runBlocking {
            repository.savePicture("UriTest")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[1].uri, "UriTest")

    }

    @Test
    fun saveTakenPhotoAlongOthers() {

        // Fill db
        runBlocking {
            repository.savePicture("Uri1")
            repository.savePicture("Uri2")
            repository.savePicture("Uri3")
        }

        // GIVEN - On the camera screen
        launchFragment()

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        IdlingRegistry.getInstance().register(resource)

        onView(matcher).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(resource)

        // Confirm photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())


        val pics = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 4)
        Assert.assertEquals(pics[0].uri, "Uri1")
        assertThat(pics[3].uri).isNotEqualTo("Uri3")

    }

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<CameraFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }
}