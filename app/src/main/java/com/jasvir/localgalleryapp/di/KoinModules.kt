package com.jasvir.localgalleryapp.di

import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.jasvir.localgalleryapp.data.source.PicGalleryDataSource
import com.jasvir.localgalleryapp.data.source.PicGalleryRepository
import com.jasvir.localgalleryapp.data.source.PicGalleryRepositoryImpl
import com.jasvir.localgalleryapp.data.source.local.PicGalleryDatabase
import com.jasvir.localgalleryapp.data.source.local.PicGalleryLocalDataSource
import com.jasvir.localgalleryapp.ui.camera.CameraViewModel
import com.jasvir.localgalleryapp.ui.gallery.GalleryViewModel
import com.jasvir.localgalleryapp.utils.helpers.dialogs.DialogManager
import com.jasvir.localgalleryapp.utils.helpers.dialogs.DialogManagerImpl
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Jasvir
 */

val AppModule = module {
    factory { (activity: FragmentActivity) -> DialogManagerImpl(activity) as DialogManager }
}

val DataModule = module {

    single {
        Room
            .databaseBuilder(androidContext(), PicGalleryDatabase::class.java, "picGallery_db")
            .build()
    }
    single { get<PicGalleryDatabase>().getPicGalleryDAO() }

    single { Dispatchers.IO }

    single { PicGalleryLocalDataSource(get(), get()) as PicGalleryDataSource }

    single { PicGalleryRepositoryImpl(get()) as PicGalleryRepository }

    viewModel { GalleryViewModel(get(), get()) }
    viewModel { CameraViewModel(get(), get()) }

}