package com.example.musicplayercompose.model

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.musicplayercompose.R

data class Song(
    val title: String,
    val songUri: Uri,
    val albumArtUri: Uri,
    var isSelected: Boolean = false,
) {
    companion object {
        fun create(
            name: String,
            songFile: Int,
            @DrawableRes songImageRes: Int,
        ): Song = Song(
            title = name,
            songUri = Uri.parse("${SongProvider.URI_PATH}$songFile"),
            albumArtUri = Uri.parse("${SongProvider.URI_PATH}$songImageRes")
        )
    }
}
