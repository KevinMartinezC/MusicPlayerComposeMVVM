package com.example.musicplayercompose.ui.playerview

import androidx.compose.runtime.MutableState
import com.example.musicplayercompose.model.Song
import kotlinx.coroutines.flow.StateFlow

data class PlayerUIState(
    val progressStateFlow: StateFlow<Int>,
    val songIndexState: MutableState<Int>,
    val currentSongState: MutableState<Song?>

)
