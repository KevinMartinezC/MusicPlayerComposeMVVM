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
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModel
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class PlayScreenFragment : Fragment() {
    private lateinit var viewModel: PlayScreenViewModel

    private val sharedViewModel: SettingScreenViewModel by activityViewModels {
        CustomViewModelFactory(SongRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            PlayScreenViewModelFactory(Application(), SongRepository, sharedViewModel)
        )[PlayScreenViewModel::class.java]
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
        val args = arguments
        val songTitle = args?.getString(SONG_TITLE_KEY).orEmpty()
        viewModel.setSongTitle(songTitle)
    }

    companion object {
        const val SONG_TITLE_KEY = "songTitle"

    }
}