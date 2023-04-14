package com.example.musicplayercompose.ui.homeview

import com.example.musicplayercompose.model.Song
import kotlinx.coroutines.flow.StateFlow

data class HomeUIState(
    val songsStateFlow: StateFlow<List<Song>>
)