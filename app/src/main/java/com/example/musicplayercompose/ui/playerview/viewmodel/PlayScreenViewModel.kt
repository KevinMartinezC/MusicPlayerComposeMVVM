package com.example.musicplayercompose.ui.playerview.viewmodel


import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.playerview.PlayerUIState
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayScreenViewModel(application: Application, songRepository: SongRepository,
                          private val sharedViewModel: SettingScreenViewModel
) :
    AndroidViewModel(application) {



    private val sliderPosition = MutableStateFlow(0f)

    private val songTitle = MutableStateFlow<String?>(null)

    private val currentSongIndex = MutableStateFlow(0)

    private val songAlbumArtUri = MutableStateFlow<Uri?>(null)

    private val playPauseButtonMutableStateFlow = MutableStateFlow(R.drawable.baseline_stop_24)


    private val songs: MutableStateFlow<List<Song>> =
        MutableStateFlow(songRepository.songs)

    val uiState =
        PlayerUIState(
            songTitle,
            songAlbumArtUri,
            currentSongIndex,
            playPauseButtonMutableStateFlow,
            sliderPosition = sliderPosition.asStateFlow()
        )

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
                delay(1000) // Update the slider position every second
            }
        }
    }

    fun setSongTitle(songTitle: String) {
        this.songTitle.value = songTitle
        val index = songs.value.indexOfFirst { it.title == songTitle }
        currentSongIndex.value = index
        songAlbumArtUri.value = songs.value.getOrNull(index)?.albumArtUri
    }

    private fun playSong(context: Context, mediaPlayerHolder: MediaPlayerHolder) {
        mediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            val songs = sharedViewModel.songs.value

            if (currentSongIndex.value !in songs.indices) {
                currentSongIndex.value = 0
            }

            val songUri = songs[currentSongIndex.value].songUri
            mediaPlayer.setDataSource(context, songUri)
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                playPauseButtonMutableStateFlow.value = R.drawable.baseline_stop_24
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
        val songs = sharedViewModel.songs.value
        val currentSongIndexValue = currentSongIndex.value
        val newSongIndex = if (currentSongIndexValue > 0) {
            currentSongIndexValue - 1
        } else {
            songs.size - 1
        }
        currentSongIndex.value = newSongIndex
        val newSong = songs.getOrNull(newSongIndex)
        songTitle.value = newSong?.title
        songAlbumArtUri.value = newSong?.albumArtUri
        playSong(context, mediaPlayerHolder)
    }

    fun onNextButtonClick(
        context: Context,
        mediaPlayerHolder: MediaPlayerHolder
    ) {
        val songs = sharedViewModel.songs.value
        val currentSongIndexValue = currentSongIndex.value
        val newSongIndex = if (currentSongIndexValue < songs.size - 1) {
            currentSongIndexValue + 1
        } else {
            0
        }
        currentSongIndex.value = newSongIndex
        val newSong = songs.getOrNull(newSongIndex)
        songTitle.value = newSong?.title
        songAlbumArtUri.value = newSong?.albumArtUri
        playSong(context, mediaPlayerHolder)
    }



}
