package com.example.musicplayercompose.ui.settingview.composablesetting

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel

@Composable
fun SongListSetting(
    viewModel: SettingScreenViewModel,
    songsState: MutableState<List<Song>>,
    paddingValues: PaddingValues,
    activity: Activity,
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = 8.dp
        )
    ) {
        items(songsState.value) { song ->
            SongListItem(
                song = song,
                onClick = { updatedSong ->
                    viewModel.handleSongClickCheckBox(songsState, updatedSong)
                },
                onDeleteSong = {
                    viewModel.handleSongDeletion(songsState, song, activity)
                },
            )
            Divider()
        }
    }
}
