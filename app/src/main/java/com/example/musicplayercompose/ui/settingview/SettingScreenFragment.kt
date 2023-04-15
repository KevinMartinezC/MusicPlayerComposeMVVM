package com.example.musicplayercompose.ui.settingview

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import coil.compose.rememberImagePainter
import com.example.music_player_mvvm.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongProvider.Companion.ALBUM_ART_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_NAME
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_PROVIDER_URI
import com.example.musicplayercompose.model.SongProvider.Companion.SONG_URI
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

class SettingScreenFragment : Fragment() {
    private val viewModel: SettingScreenViewModel by activityViewModels {
        CustomViewModelFactory(SongRepository)
    }
    private lateinit var songs: MutableList<Song>
    private val songListState = mutableStateOf<List<Song>>(listOf())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadSongsFromProvider() // Make sure to call this before setting up the content
        addNewSongsToProvider()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {  },
                                content = {
                                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_song_button_description))
                                }
                            )
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        isFloatingActionButtonDocked = true
                    ) { contentPadding ->
                        SongListSetting(
                            songs = songListState.value,
                            paddingValues = contentPadding,
                            onSongClick = { song ->


                            }
                        )
                    }
                }
            }


        }
    }
    private fun addNewSongsToProvider() {

        val newSongs = listOf(
            Song.create(SONG_NAME_FOUR, R.raw.song4, R.drawable.album_art_4),
            Song.create(SONG_NAME_FIVE, R.raw.song5, R.drawable.album_art_5),
            Song.create(SONG_NAME_SIX, R.raw.song6, R.drawable.album_art_6),
            Song.create(SONG_NAME_SEVEN, R.raw.song7, R.drawable.album_art_7),
            Song.create(SONG_NAME_EIGHT, R.raw.song8, R.drawable.album_art_8),
            Song.create(SONG_NAME_NINE, R.raw.song9, R.drawable.album_art_9),
            Song.create(SONG_NAME_TEN, R.raw.song10, R.drawable.album_art_10),
        )
        newSongs.forEach { song ->
            val contentValues = ContentValues().apply {
                put(SONG_NAME, song.title)
                put(SONG_URI, song.songUri.toString())
                put(ALBUM_ART_URI, song.albumArtUri.toString())
            }
            requireActivity().contentResolver.insert(SONG_PROVIDER_URI, contentValues)
        }
        loadSongsFromProvider()
    }

    private fun loadSongsFromProvider() {
        songs = viewModel.fetchSongsFromProvider(requireActivity())
        songListState.value = viewModel.fetchSongsFromProvider(requireActivity())
    }

    companion object {
        const val SONG_NAME_FOUR: String = "Sia - Chandelier "
        const val SONG_NAME_FIVE: String = "Camila Cabello - Havana"
        const val SONG_NAME_SIX: String = "MAGIC! - Rude "
        const val SONG_NAME_SEVEN: String = "Alan Walker - Faded"
        const val SONG_NAME_EIGHT: String = "Adele - Someone Like You "
        const val SONG_NAME_NINE: String = "John Legend - All of Me "
        const val SONG_NAME_TEN: String = "Avicii ft - Wake Me Up "
        const val SONG_NAME = "song_name"
        const val SONG_URI = "song_uri"
        const val ALBUM_ART_URI = "album_art_uri"
    }
}

@Composable
fun SongListSetting(
    songs: List<Song>,
    paddingValues: PaddingValues,
    onSongClick: (Song) -> Unit
) {
    // Create a stateful list of songs
    val songListState = remember { mutableStateListOf(*songs.toTypedArray()) }

    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = 8.dp
        )
    ) {
        items(songListState) { song ->
            SongListItem(
                song = song,
                onClick = { updatedSong ->
                    // Update the song selection state in the list
                    var index = -1
                    for (i in songListState.indices) {
                        if (songListState[i].songUri == updatedSong.songUri) {
                            index = i
                            break
                        }
                    }

                    if (index >= 0) {
                        songListState[index] = updatedSong.copy(isSelected = updatedSong.isSelected)
                    }
                },
                onDeleteClick = { /* Handle delete click */ },
            )
            Divider()
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    onClick: (Song) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(data = song.albumArtUri),
            contentDescription = stringResource(R.string.album_art),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(song.title, modifier = Modifier.weight(1f))

        // Add the Checkbox for multiple selection
        Checkbox(
            checked = song.isSelected,
            onCheckedChange = { isChecked ->
                onClick(song.copy(isSelected = isChecked))
            }
        )

        // Add the delete button
        IconButton(onClick = onDeleteClick) {
            val deleteIcon = Icons.Default.Delete
            Icon(deleteIcon, contentDescription = stringResource(R.string.delete_song_button_description))
        }
    }
}
