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
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModel
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModelFactory

class PlayScreenFragment : Fragment() {
    private lateinit var viewModel: PlayScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            PlayScreenViewModelFactory(Application(), SongRepository)
        )[PlayScreenViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    PlayScreen(viewModel)
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