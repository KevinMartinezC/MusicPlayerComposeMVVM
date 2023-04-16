package com.example.musicplayercompose.ui.homeview.viewmodel


import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.homeview.HomeUIState
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
            collectAddedSongs() // Add this line
        }
    }

    private suspend fun collectAddedSongs() {
        sharedViewModel.addedSongs.collect { song ->
            songsMutableState.value = songsMutableState.value + song
        }
    }

    private fun fetchSongs(activity: Activity) {
        val defaultSongs = songRepository.getDefaultSongs()
        Log.d("HomeScreenViewModel", "Default songs: $defaultSongs")

        val providerSongs = sharedViewModel.fetchSongsFromProvider(activity)
        Log.d("HomeScreenViewModel", "Provider songs: $providerSongs")

        val combinedSongs = mutableListOf<Song>().apply {
            addAll(defaultSongs)
            addAll(providerSongs.filter { song -> !defaultSongs.contains(song) })
        }
        Log.d("HomeScreenViewModel", "Combined songs: $combinedSongs")

        songsMutableState.value = combinedSongs
    }


}