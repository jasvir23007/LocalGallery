package com.jasvir.localgalleryapp

import android.app.Application
import com.jasvir.localgalleryapp.di.AppModule
import com.jasvir.localgalleryapp.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @author Jasvir
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(listOf(AppModule, DataModule))
        }
    }
}