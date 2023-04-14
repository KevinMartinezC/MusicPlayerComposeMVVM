package com.example.musicplayercompose.ui.homeview

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.musicplayercompose.R
import com.example.musicplayercompose.databinding.FragmentHomeScreenBinding
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory


class HomeScreenFragment : Fragment() {
    private lateinit var viewModel: HomeScreenViewModel
    private var currentSongIndex: Int = 0
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(
            this,
            HomeScreenViewModelFactory(requireContext())
        )[HomeScreenViewModel::class.java]

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val songs by viewModel.uiState.songsStateFlow.collectAsState()
                    SongList(songs) { song ->
                        onSongClick(song)
                    }
                }
            }
        }
        return view
    }

    private fun onSongClick(song: Song) {
        val position = viewModel.uiState.songsStateFlow.value.indexOf(song)
        playSelectedSong(position)
        findNavController().navigate(R.id.action_homeScreenFragment_to_playScreenFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

@Composable
fun SongList(songs: List<Song>, onSongClick: (Song) -> Unit) {
    LazyColumn {
        items(songs) { song ->
            SongListItem(song, onSongClick)
            Divider()
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SongListItem(song: Song, onClick: (Song) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick(song) }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = song.albumArtUri),
            contentDescription = stringResource(R.string.album_art),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(song.title)
    }
}
