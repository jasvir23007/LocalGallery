package com.jasvir.localgalleryapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jasvir.localgalleryapp.data.models.ImageUri

/**
 * @author Jasvir
 */
@Dao
interface PicGalleryDAO {

    /**
     * Observes list of uris.
     *
     * @return all uris.
     */
    @Query("SELECT * FROM uris LIMIT :page , :per_page")
    fun observeUris(page: Int, per_page: Int): LiveData<List<ImageUri>>

    /**
     * Delete all uris.
     */
    @Query("DELETE FROM uris")
    suspend fun deletePictures()

    /**
     * Save image uri.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePic(picture: ImageUri)

    /**
     * Fetch uris.
     */
    @Query("SELECT * FROM uris")
    fun fetchUris() : List<ImageUri>

}