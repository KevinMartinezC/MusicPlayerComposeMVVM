package com.example.musicplayercompose.ui.homeview.viewmodel


import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class HomeScreenViewModel(
    context: Context,
    private val songRepository: SongRepository,
    private val sharedViewModel: SettingScreenViewModel

) : ViewModel() {
    init {
        SongRepository.initialize(context)
    }

    private val songsMutableState = MutableStateFlow<List<Song>>(emptyList())
    val uiState = HomeUIState(songsMutableState)

    init {
        viewModelScope.launch {
            fetchSongs(context as Activity)
            collectAddedSongs()
        }
    }

    fun playFirstSong(): Song? {
        val songs = uiState.songsStateFlow.value
        return if (songs.isNotEmpty()) {
            songs[0]
        } else {
            null
        }
    }
    fun playRandomSong(): Song? {
        val songs = uiState.songsStateFlow.value
        return if (songs.isNotEmpty()) {
            val randomSongIndex = (songs.indices).random()
            songs[randomSongIndex]
        } else {
            null
        }
    }
    fun onSongClick(song: Song): Int {
        return uiState.songsStateFlow.value.indexOf(song)
    }
    private suspend fun collectAddedSongs() {
        sharedViewModel.addedSongs.collect { song ->
            songsMutableState.value = songsMutableState.value + song
        }
    }

    fun refreshSongs() {
        viewModelScope.launch {
            val defaultSongs = songRepository.getDefaultSongs()
            songsMutableState.value = defaultSongs
        }
    }

    private fun fetchSongs(activity: Activity) {
        val defaultSongs = songRepository.getDefaultSongs()

        val providerSongs = sharedViewModel.fetchSongsFromProvider(activity)

        val combinedSongs = mutableListOf<Song>().apply {
            addAll(defaultSongs)
            addAll(providerSongs.filter { song -> !defaultSongs.contains(song) })
        }
        songsMutableState.value = combinedSongs
    }
}