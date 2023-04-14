package com.example.musicplayercompose.ui.homeview

import com.example.musicplayercompose.model.Song
import kotlinx.coroutines.flow.StateFlow

data class UIState(
    val songsStateFlow: StateFlow<List<Song>>
)