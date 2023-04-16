package com.example.musicplayercompose.ui.settingview.viewmodel

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongProvider.Companion.ALBUM_ART_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_NAME
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_PROVIDER_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_URI
import com.example.musicplayercompose.model.SongRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingScreenViewModel(songRepository: SongRepository) : ViewModel() {
    private val _songs = MutableStateFlow(songRepository.getDefaultSongs())
    val songs: StateFlow<List<Song>> = _songs

    private val _addedSongs = MutableSharedFlow<Song>() // Add this line
    val addedSongs: SharedFlow<Song> = _addedSongs // Add this line

    fun addNewSongs(newSongs: List<Song>) {
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
}
