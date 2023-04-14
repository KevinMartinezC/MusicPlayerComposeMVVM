package com.example.musicplayercompose.ui.playerview.viewmodel


import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.playerview.PlayerUIState
import kotlinx.coroutines.flow.MutableStateFlow

class PlayScreenViewModel(application: Application, songRepository: SongRepository) :
    AndroidViewModel(application) {

    private val songTitle = MutableStateFlow<String?>(null)

    private val _currentSongIndex = MutableStateFlow<Int?>(null)

    private val songAlbumArtUri = MutableStateFlow<Uri?>(null)


    private val songs: MutableStateFlow<List<Song>> =
        MutableStateFlow(songRepository.songs)

    val uiState = PlayerUIState(songTitle, songAlbumArtUri)

    fun setSongTitle(songTitle: String) {
        this.songTitle.value = songTitle
        val index = songs.value.indexOfFirst { it.title == songTitle }
        _currentSongIndex.value = index
        songAlbumArtUri.value = songs.value.getOrNull(index)?.albumArtUri
    }


}
