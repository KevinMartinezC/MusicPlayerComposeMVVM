package com.example.musicplayercompose.ui.playerview.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class PlayScreenViewModelFactory(
    private val application: Application,
    private val repository: SongRepository,
    private val shareViewModel: SettingScreenViewModel,
    private val homeScreenViewModel: HomeScreenViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayScreenViewModel(application, repository, shareViewModel, homeScreenViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
