package com.example.musicplayercompose.ui.playerview

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModel
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class PlayScreenFragment : Fragment() {
    private val viewModel: PlayScreenViewModel by activityViewModels {
        PlayScreenViewModelFactory(
            Application(),
            SongRepository,
            sharedViewModel,
            homeScreenViewModel
        )
    }

    private val sharedViewModel: SettingScreenViewModel by activityViewModels {
        CustomViewModelFactory(SongRepository)
    }
    private val homeScreenViewModel: HomeScreenViewModel by activityViewModels {
        HomeScreenViewModelFactory(requireContext(), SongRepository, sharedViewModel)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    PlayScreen(viewModel, MediaPlayerHolder)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSongInfo()
    }
    


    private fun initSongInfo() {
        viewModel.uiState.songTitle.value?.let { songTitle ->
            if (songTitle.isEmpty()) {
                val args = arguments
                val newSongTitle = args?.getString(SONG_TITLE_KEY).orEmpty()
                val song = viewModel.homeScreenViewModel.uiState.songsStateFlow.value.find { it.title == newSongTitle }
                if (song != null) {
                    viewModel.setSongTitle(song) // Pass the Song object
                }
            }
        }
    }


    companion object {
        const val SONG_TITLE_KEY = "songTitle"

    }
}