package com.example.musicplayercompose.ui.homeview.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.homeview.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class HomeScreenViewModel(context: Context) : ViewModel() {
    init {
        SongRepository.initialize(context)
    }

    private val songsMutableState = MutableStateFlow<List<Song>>(emptyList())

    val uiState = UIState(songsMutableState)

    init {
        viewModelScope.launch {
            fetchSongs()
        }
    }

    private fun fetchSongs() {
        val songs = SongRepository.getDefaultSongs()
        songsMutableState.value = songs
    }
}