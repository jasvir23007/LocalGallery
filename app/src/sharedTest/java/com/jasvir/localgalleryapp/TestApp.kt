package com.jasvir.localgalleryapp


import android.app.Application
import com.jasvir.localgalleryapp.di.AppModule
import com.jasvir.localgalleryapp.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * @author Jasvir
 */
class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TestApp)
            modules(listOf(AppModule, DataModule))
        }
    }

    internal fun injectModule(module: Module) {
        loadKoinModules(module)
    }
}