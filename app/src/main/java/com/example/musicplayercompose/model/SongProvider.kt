package com.example.musicplayercompose.model

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.musicplayercompose.R

enum class AffectedRow {
    None,
    OneLine,
}

class SongProvider : ContentProvider() {
    private val _deletedSongs = mutableSetOf<String>()

    private val _songs = mutableListOf(
        Song.create(SONG_NAME_ONE, R.raw.song1, R.drawable.album_art_1),
        Song.create(SONG_NAME_TWO, R.raw.song2, R.drawable.album_art_2),
        Song.create(SONG_NAME_THREE, R.raw.song3, R.drawable.album_art_3)
    )

    val songs: List<Song>
        get() = _songs.toList()

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val matrixCursor = MatrixCursor(arrayOf(ID, SONG_NAME, SONG_URI, ALBUM_ART_URI))
        songs.forEachIndexed { index, song ->

            val isSongAvailable = _deletedSongs.contains(song.title)
            if (!isSongAvailable) {
                matrixCursor.addRow(
                    arrayOf(
                        index,
                        song.title,
                        song.songUri.toString(),
                        song.albumArtUri.toString()
                    )
                )
            }
        }

        return matrixCursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {

        requireNotNull(values) { "ContentValues cannot be null" }

        val title = values.getAsString(SONG_NAME)
        val songUri = Uri.parse(values.getAsString(SONG_URI))
        val albumArtUri = Uri.parse(values.getAsString(ALBUM_ART_URI))

        Song(title, songUri, albumArtUri).also(_songs::add)

        return ContentUris.withAppendedId(uri, (_songs.size - 1).toLong())
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not supported. Read-only provider.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val id = ContentUris.parseId(uri).toInt()
        return if (id in _songs.indices) {
            _deletedSongs.add(_songs[id].title)
            _songs.removeAt(id)
            AffectedRow.OneLine.ordinal
        } else {
            AffectedRow.None.ordinal
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        const val SONG_NAME_ONE = "Bar Liar"
        const val SONG_NAME_TWO = "Girls Like You"
        const val SONG_NAME_THREE = "See You Again"
        private const val AUTHORITY = "com.example.musicplayercompose.provider"
        const val URI_PATH = "android.resource://com.example.musicplayercompose/"
        const val ID = "_id"
        const val SONG_NAME = "song_name"
        const val SONG_URI = "song_uri"
        const val ALBUM_ART_URI = "album_art_uri"
        val SONG_PROVIDER_URI: Uri = Uri.parse("content://$AUTHORITY/songs")

        fun getSongsFromCursor(cursor: Cursor): List<Song> {
            val songs = mutableListOf<Song>()

            val nameColumnIndex = cursor.getColumnIndex(SONG_NAME)
            val songUriColumnIndex = cursor.getColumnIndex(SONG_URI)
            val albumArtUriColumnIndex = cursor.getColumnIndex(ALBUM_ART_URI)

            if (nameColumnIndex == -1 || songUriColumnIndex == -1 || albumArtUriColumnIndex == -1) {
                return emptyList()
            }
            while (cursor.moveToNext()) {
                val title = cursor.getString(nameColumnIndex)
                val songUri = Uri.parse(cursor.getString(songUriColumnIndex))
                val albumArtUri = Uri.parse(cursor.getString(albumArtUriColumnIndex))
                val song = Song(title, songUri, albumArtUri)
                songs.add(song)
            }
            return songs
        }

    }
}
