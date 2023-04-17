package com.example.musicplayercompose.ui.homeview

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.composablehome.BottomBarActions
import com.example.musicplayercompose.ui.homeview.composablehome.SongList
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory
import com.example.musicplayercompose.ui.playerview.PlayScreenFragment
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import com.example.musicplayercompose.utils.composeView


class HomeScreenFragment : Fragment() {
    private val viewModel: HomeScreenViewModel by activityViewModels {
        HomeScreenViewModelFactory(requireContext(), SongRepository, sharedViewModel)
    }
    private var currentSongIndex: Int = 0
    private val sharedViewModel: SettingScreenViewModel by activityViewModels {
        CustomViewModelFactory(SongRepository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return composeView {
            val songs by viewModel.uiState.songsStateFlow.collectAsState()
            Scaffold(
                bottomBar = {
                    BottomBarActions(
                        onSettingsClick = { navigateToSettings() },
                        onPlayFirstSongClick = { playFirstSong() },
                        onPlayRandomSongClick = { playRandomSong() }
                    )
                }
            ) { paddingValues ->
                SongList(songs, paddingValues, onSongClick = { song ->
                    onSongClick(song)
                }, onRefresh = {
                    viewModel.refreshSongs()
                })
            }
        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_homeScreenFragment_to_settingScreenFragment)
    }

    private fun navigateToDetailActivity(position: Int) {
        val songs = viewModel.uiState.songsStateFlow.value
        val bundle = Bundle().apply {
            putString(PlayScreenFragment.SONG_TITLE_KEY, songs[position].title)
        }
        findNavController().navigate(R.id.action_homeScreenFragment_to_playScreenFragment, bundle)
    }

    private fun playSelectedSong(position: Int) {
        MediaPlayerHolder.mediaPlayer?.release()
        currentSongIndex = position
        MediaPlayerHolder.mediaPlayer = MediaPlayer.create(
            requireContext(),
            viewModel.uiState.songsStateFlow.value[position].songUri
        )
        MediaPlayerHolder.mediaPlayer?.start()
    }

    private fun playFirstSong() {
        viewModel.playFirstSong()?.let { song ->
            onSongClick(song)
        }
    }

    private fun playRandomSong() {
        viewModel.playRandomSong()?.let { song ->
            onSongClick(song)
        }
    }

    private fun onSongClick(song: Song) {
        val position = viewModel.onSongClick(song)
        playSelectedSong(position)
        navigateToDetailActivity(position)
    }

}

