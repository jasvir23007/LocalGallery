package com.jasvir.localgalleryapp.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jasvir.localgalleryapp.data.models.ImageUri

/**
 * @author Jasvir
 */
@Database(entities = [ImageUri::class], version = 1, exportSchema = false)
abstract class PicGalleryDatabase : RoomDatabase() {
    abstract fun getPicGalleryDAO() : PicGalleryDAO
}