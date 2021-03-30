package com.jasvir.localgalleryapp.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @author Jasvir
 */

@Entity(tableName = "uris")
data class ImageUri (
    @NonNull
    @PrimaryKey
    var uri : String
) : Serializable