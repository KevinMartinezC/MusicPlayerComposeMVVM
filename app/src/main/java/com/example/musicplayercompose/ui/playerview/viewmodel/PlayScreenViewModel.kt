package com.example.musicplayercompose.ui.playerview.viewmodel


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayScreenViewModel(
    val homeScreenViewModel: HomeScreenViewModel,
) : ViewModel(){

    private var isSongTitleInitialized = false

    private val sliderPosition = MutableStateFlow(0f)

    private val songTitle = MutableStateFlow<String?>(null)

    private val currentSongIndex = MutableStateFlow(0)

    private val songAlbumArtUri = MutableStateFlow<Uri?>(null)

    private val playPauseButtonMutableStateFlow = MutableStateFlow(R.drawable.baseline_stop_24)

    private val songs: StateFlow<List<Song>> = homeScreenViewModel.uiState.songsStateFlow


    val uiState =
        PlayerUIState(
            songTitle,
            songAlbumArtUri,
            currentSongIndex,
            playPauseButtonMutableStateFlow,
            sliderPosition = sliderPosition.asStateFlow()
        )

    val currentSong: StateFlow<Song?> = currentSongIndex.map { index ->
        songs.value.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


    fun onSliderPositionChanged(newSliderPosition: Float) {
        MediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            val newPosition = (newSliderPosition * mediaPlayer.duration).toInt()
            mediaPlayer.seekTo(newPosition)
        }
    }

    fun updateSliderPosition(mediaPlayerHolder: MediaPlayerHolder) {
        viewModelScope.launch {
            while (true) {
                mediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition.toFloat()
                        val duration = mediaPlayer.duration.toFloat()
                        val progress = currentPosition / duration
                        sliderPosition.value = progress
                    }
                }
                delay(1000)
            }
        }
    }

    fun setSongTitle(song: Song) {
        this.songTitle.value = song.title
        val index = songs.value.indexOfFirst { it == song }
        currentSongIndex.value = index
        songAlbumArtUri.value = song.albumArtUri
        isSongTitleInitialized = true
    }

    private fun playSong(context: Context, mediaPlayerHolder: MediaPlayerHolder) {
        mediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            val songs = homeScreenViewModel.uiState.songsStateFlow.value

            if (currentSongIndex.value !in songs.indices) {
                currentSongIndex.value = INITIAL_VALUE_INDEX
            }

            val song = songs[currentSongIndex.value]
            mediaPlayer.setDataSource(context, song.songUri)
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                playPauseButtonMutableStateFlow.value = R.drawable.baseline_stop_24
                setSongTitle(song)
            }
        }
    }

    fun onPlayPauseButtonClick() {
        MediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playPauseButtonMutableStateFlow.value = R.drawable.outline_play_arrow_24
            } else {
                mediaPlayer.start()
                playPauseButtonMutableStateFlow.value = R.drawable.baseline_stop_24
            }
        }
    }

    fun onPreviousButtonClick(
        context: Context,
        mediaPlayerHolder: MediaPlayerHolder
    ) {
        viewModelScope.launch {
            songs.collect { songs ->
                val currentSongIndexValue = currentSongIndex.value
                val newSongIndex = if (currentSongIndexValue > 0) {
                    currentSongIndexValue - 1
                } else {
                    songs.size - 1
                }
                currentSongIndex.value = newSongIndex
                val newSong = songs.getOrNull(newSongIndex)
                newSong?.let {
                    setSongTitle(it)
                    playSong(context, mediaPlayerHolder)
                }
            }
        }
    }

    fun onNextButtonClick(
        context: Context,
        mediaPlayerHolder: MediaPlayerHolder
    ) {
        viewModelScope.launch {
            songs.collect { songs ->
                val currentSongIndexValue = currentSongIndex.value
                val newSongIndex = if (currentSongIndexValue < songs.size - 1) {
                    currentSongIndexValue + 1
                } else {
                    INITIAL_VALUE_INDEX
                }
                currentSongIndex.value = newSongIndex
                val newSong = songs.getOrNull(newSongIndex)
                newSong?.let {
                    setSongTitle(it)
                    playSong(context, mediaPlayerHolder)
                }
            }
        }
    }

    companion object {
        const val INITIAL_VALUE_INDEX = 0
    }
}
