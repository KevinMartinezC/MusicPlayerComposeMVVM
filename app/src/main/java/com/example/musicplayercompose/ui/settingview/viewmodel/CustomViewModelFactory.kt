package com.example.music_player_mvvm.ui.settingview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class CustomViewModelFactory(private val songRepository: SongRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingScreenViewModel(songRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
