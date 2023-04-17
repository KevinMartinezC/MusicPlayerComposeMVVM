package com.example.musicplayercompose.ui.playerview.viewmodel

import android.net.Uri
import com.example.musicplayercompose.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlayerUIState(
    val songTitle: StateFlow<String?> = MutableStateFlow(null),
    val image: StateFlow<Uri?> = MutableStateFlow(Uri.EMPTY),
    val index: StateFlow<Int> = MutableStateFlow(0),
    val playPauseButton: StateFlow<Int> = MutableStateFlow(R.drawable.outline_play_arrow_24),
    val sliderPosition: StateFlow<Float> = MutableStateFlow(0f)
)
