package com.example.musicplayercompose.ui.homeview

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory
import com.example.musicplayercompose.ui.playerview.PlayScreenFragment
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import com.example.musicplayercompose.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch


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

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyApplicationTheme{
                    val songs by viewModel.uiState.songsStateFlow.collectAsState()
                    Scaffold(
                        bottomBar = { BottomBarActions(onSettingsClick = { navigateToSettings() },
                            onPlayFirstSongClick = { playFirstSong()},
                            onPlayRandomSongClick = { playRandomSong() }

                        ) }
                    ) { paddingValues ->
                        SongList(songs, paddingValues, onSongClick = { song ->
                            onSongClick(song)
                        }, onRefresh = {
                            viewModel.refreshSongs() // Implement this method in your ViewModel
                        })
                    }
                }
            }

        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_homeScreenFragment_to_settingScreenFragment)
    }
    private fun playFirstSong() {
        val songs = viewModel.uiState.songsStateFlow.value
        if (songs.isNotEmpty()) {
            onSongClick(songs[0])
        }
    }
    private fun playRandomSong() {
        val songs = viewModel.uiState.songsStateFlow.value
        if (songs.isNotEmpty()) {
            val randomSongIndex = (songs.indices).random()
            onSongClick(songs[randomSongIndex])
        }
    }

    private fun onSongClick(song: Song) {
        val position = viewModel.uiState.songsStateFlow.value.indexOf(song)
        playSelectedSong(position)

        navigateToDetailActivity(position)
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

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SongList(
    songs: List<Song>,
    paddingValues: PaddingValues,
    onSongClick: (Song) -> Unit,
    onRefresh: suspend () -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(songs.size) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        onRefresh()
        itemCount = songs.size
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(Modifier.pullRefresh(state)) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!refreshing) {
                items(songs) { song ->
                    SongListItem(song, onSongClick)
                    Divider()
                }
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun BottomBarActions(
    onSettingsClick: () -> Unit,
    onPlayFirstSongClick: () -> Unit,
    onPlayRandomSongClick: () -> Unit


) {
    BottomAppBar {
        IconButton(

            onClick = onSettingsClick,
            //modifier = Modifier.background(MaterialTheme.colors.primary)

        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onPlayRandomSongClick

        ) {
            Icon(
                Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.random_song)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onPlayFirstSongClick

        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.play_first_song)
            )
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
        Text(song.title,  color = MaterialTheme.colorScheme.secondary)
    }
}

