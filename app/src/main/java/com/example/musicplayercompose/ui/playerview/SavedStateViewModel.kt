package com.example.musicplayercompose.ui.playerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavedStateViewModel : ViewModel() {
    private val _songTitle = MutableLiveData<String?>(null)
    val songTitle: LiveData<String?> get() = _songTitle

    fun setSongTitle(songTitle: String) {
        _songTitle.value = songTitle
    }
}
