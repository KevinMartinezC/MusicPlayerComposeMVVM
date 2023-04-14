package com.example.musicplayercompose.ui.playerview

import android.net.Uri
import androidx.compose.runtime.MutableState
import com.example.musicplayercompose.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlayerUIState(
 val sonTitle : StateFlow<String?> = MutableStateFlow(""),
 val image : StateFlow<Uri?> = MutableStateFlow(Uri.EMPTY)

)
