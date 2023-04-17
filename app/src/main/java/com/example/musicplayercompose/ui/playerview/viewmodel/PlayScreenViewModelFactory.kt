package com.example.musicplayercompose.ui.playerview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel

class PlayScreenViewModelFactory(
    private val homeScreenViewModel: HomeScreenViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayScreenViewModel( homeScreenViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
