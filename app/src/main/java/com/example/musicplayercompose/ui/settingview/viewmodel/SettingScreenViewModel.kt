package com.example.musicplayercompose.ui.settingview.viewmodel

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongProvider.Companion.ALBUM_ART_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_NAME
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_PROVIDER_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_URI
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_EIGHT
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_FIVE
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_FOUR
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_NINE
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_SEVEN
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_SIX
import com.example.musicplayercompose.ui.settingview.SettingScreenFragment.Companion.SONG_NAME_TEN
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingScreenViewModel(songRepository: SongRepository) : ViewModel() {
    private val _songs = MutableStateFlow(songRepository.getDefaultSongs())
    val songs: StateFlow<List<Song>> = _songs

    private val _addedSongs = MutableSharedFlow<Song>()
    val addedSongs: SharedFlow<Song> = _addedSongs

    private fun addNewSongs(newSongs: List<Song>) {
        val nonDuplicateSongs = newSongs.filter { newSong ->
            !_songs.value.any { existingSong ->
                existingSong.title == newSong.title
            }
        }
        _songs.value = _songs.value + nonDuplicateSongs

        viewModelScope.launch {
            nonDuplicateSongs.forEach { song ->
                _addedSongs.emit(song)
            }
        }
    }

    fun fetchSongsFromProvider(activity: Activity): MutableList<Song> {
        val cursor = activity.contentResolver.query(
            SONG_PROVIDER_URI,
            null,
            null,
            null,
            null
        )

        val songs = mutableListOf<Song>()

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val title = it.getString(it.getColumnIndexOrThrow(SONG_NAME))
                    val songUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(SONG_URI)))
                    val albumArtUri =
                        Uri.parse(it.getString(it.getColumnIndexOrThrow(ALBUM_ART_URI)))

                    val song = Song(title, songUri, albumArtUri)
                    if (!songs.contains(song)) {
                        songs.add(song)
                    }

                } catch (e: IllegalArgumentException) {
                    Log.e(
                        "SettingScreenViewModel",
                        "Error reading song data from provider: ${e.message}"
                    )
                }
            }
        }
        return songs.distinct().toMutableList()
    }

    fun addNewSongsToProvider(
        contentResolver: ContentResolver,
        loadSongsFromProvider: () -> Unit
    ) {
        val newSongs = listOf(
            Song.create(SONG_NAME_FOUR, R.raw.song4, R.drawable.album_art_4),
            Song.create(SONG_NAME_FIVE, R.raw.song5, R.drawable.album_art_5),
            Song.create(SONG_NAME_SIX, R.raw.song6, R.drawable.album_art_6),
            Song.create(SONG_NAME_SEVEN, R.raw.song7, R.drawable.album_art_7),
            Song.create(SONG_NAME_EIGHT, R.raw.song8, R.drawable.album_art_8),
            Song.create(SONG_NAME_NINE, R.raw.song9, R.drawable.album_art_9),
            Song.create(SONG_NAME_TEN, R.raw.song10, R.drawable.album_art_10),
        )
        newSongs.forEach { song ->
            val contentValues = ContentValues().apply {
                put(SONG_NAME, song.title)
                put(SONG_URI, song.songUri.toString())
                put(ALBUM_ART_URI, song.albumArtUri.toString())
            }
            contentResolver.insert(SONG_PROVIDER_URI, contentValues)
        }
        loadSongsFromProvider()
    }


    fun addSelectedSongsToHomeScreen(
        songsState: MutableState<List<Song>>,
        contentResolver: ContentResolver
    ) {
        val selectedSongs = songsState.value.filter { it.isSelected }
        addNewSongs(selectedSongs)

        for (song in selectedSongs) {
            val contentValues = ContentValues().apply {
                put(SONG_NAME, song.title)
                put(SONG_URI, song.songUri.toString())
                put(ALBUM_ART_URI, song.albumArtUri.toString())
            }
            contentResolver.insert(SONG_PROVIDER_URI, contentValues)
        }

        songsState.value = songsState.value.map { song ->
            song.copy(isSelected = false)
        }
    }

    fun handleSongClickCheckBox(songsState: MutableState<List<Song>>, updatedSong: Song) {
        var index = -1
        for (i in songsState.value.indices) {
            if (songsState.value[i].songUri == updatedSong.songUri) {
                index = i
                break
            }
        }
        if (index >= 0) {
            songsState.value = songsState.value.toMutableList().apply {
                this[index] = updatedSong.copy(isSelected = updatedSong.isSelected)
            }
        }
    }

    fun handleSongDeletion(songsState: MutableState<List<Song>>, song: Song, activity: Activity) {
        val songIndex = songsState.value.indexOf(song)
        if (songIndex >= 0) {
            val songUriWithId = ContentUris.withAppendedId(SONG_PROVIDER_URI, songIndex.toLong())
            activity.contentResolver.delete(songUriWithId, null, null)
            songsState.value = songsState.value.toMutableList().apply {
                removeAt(songIndex)
            }
        }
    }

}
