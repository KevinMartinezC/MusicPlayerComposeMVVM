package com.example.musicplayercompose.ui.playerview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory
import com.example.musicplayercompose.ui.playerview.composableplayer.PlayScreen
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModel
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import com.example.musicplayercompose.utils.composeView

class PlayScreenFragment : Fragment() {
    private val viewModel: PlayScreenViewModel by activityViewModels {
        PlayScreenViewModelFactory(
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
        return composeView {
            PlayScreen(viewModel, MediaPlayerHolder)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSongInfo()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        initInitialSongTitle()
    }

    private fun initInitialSongTitle() {
        val args = arguments
        val newSongTitle = args?.getString(SONG_TITLE_KEY).orEmpty()
        viewModel.homeScreenViewModel.uiState.songsStateFlow.value.find { it.title == newSongTitle }
            ?.let { song ->
                viewModel.setSongTitle(song)
            }
    }

    private fun initSongInfo() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currentSong.collect { song ->
                if (song != null) {
                    viewModel.setSongTitle(song)
                }
            }
        }
    }

    companion object {
        const val SONG_TITLE_KEY = "songTitle"

    }
}