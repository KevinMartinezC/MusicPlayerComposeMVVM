package com.example.musicplayercompose.ui.playerview.viewmodel


import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.playerview.PlayerUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())


    private var updateSeekBarJob: Job? = null

    private val progressMutableState: MutableStateFlow<Int> = MutableStateFlow(INITIAL_PROGRESS_VALUE)
    private val songIndexState = mutableStateOf(0)
    val currentSongState = mutableStateOf<Song?>(null)

    val uiState = PlayerUIState(progressMutableState, songIndexState, currentSongState)

    private val playPauseButtonState = mutableStateOf(android.R.drawable.ic_media_play)


    private val playbackPositionMutableLiveData = MutableLiveData<Int>()
    val playbackPositionLiveData: LiveData<Int> = playbackPositionMutableLiveData

    fun updatePlaybackPosition(position: Int) {
        playbackPositionMutableLiveData.postValue(position)
    }

    fun playSong() {
        startUpdatingSeekBar()
    }
    private fun startUpdatingSeekBar() {
        updateSeekBarJob = viewModelScope.launch(Dispatchers.Main) {
            updateSeekBar()
        }
    }

    private suspend fun updateSeekBar() {
        MediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            progressMutableState.tryEmit(mediaPlayer.currentPosition)
            delay(1000)
            updateSeekBar()
        }
    }

    private fun stopUpdatingSeekBar() {
        updateSeekBarJob?.cancel()
    }
    fun onPlayPauseButtonClick() {
        MediaPlayerHolder.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                stopUpdatingSeekBar()
                playPauseButtonState.value = android.R.drawable.ic_media_play
            } else {
                mediaPlayer.start()
                startUpdatingSeekBar()
                playPauseButtonState.value = android.R.drawable.ic_media_pause
            }
        }
    }
    fun onPreviousButtonClick(songs: List<Song>) {
        val currentSongIndex = songIndexState.value
        val newSongIndex = if (currentSongIndex > 0) {
            currentSongIndex - 1
        } else {
            songs.size - 1
        }
        songIndexState.value = newSongIndex
        updateCurrentSong(songs, newSongIndex)
    }

    fun onNextButtonClick(songs: List<Song>) {
        val currentSongIndex = songIndexState.value
        val newSongIndex = if (currentSongIndex < songs.size - 1) {
            currentSongIndex + 1
        } else {
            INITIAL_INDEX
        }
        songIndexState.value = newSongIndex
        updateCurrentSong(songs, newSongIndex)
    }


    private fun sendSongChangedBroadcast(currentSong: Song) {
        val intent = Intent(ACTION_SONG_CHANGED).apply {
            putExtra(SONG_TITLE, currentSong.title)
            putExtra(SONG_URI, currentSong.songUri.toString())
            putExtra(ALBUM_ART_URI, currentSong.albumArtUri.toString())
        }

        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent)
    }

    private fun updateCurrentSong(songs: List<Song>, currentSongIndex: Int) {
        songs.getOrNull(currentSongIndex)?.let { currentSong ->
            currentSongState.value = currentSong
            sendSongChangedBroadcast(currentSong)
        }
    }

    companion object {
        const val ACTION_SONG_CHANGED = "com.example.music_player_mvvm.ACTION_SONG_CHANGED"
        const val SONG_TITLE = "song_title"
        const val SONG_URI = "song_uri"
        const val ALBUM_ART_URI = "album_art_uri"
        const val INITIAL_PROGRESS_VALUE = 0
        const val INITIAL_INDEX = 0
    }
}
