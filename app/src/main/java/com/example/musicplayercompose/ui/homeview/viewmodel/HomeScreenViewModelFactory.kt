package com.example.musicplayercompose.ui.homeview.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class HomeScreenViewModelFactory(
    private val context: Context,
    private val songRepository: SongRepository,
    private val sharedViewModel: SettingScreenViewModel,
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(context, songRepository, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
