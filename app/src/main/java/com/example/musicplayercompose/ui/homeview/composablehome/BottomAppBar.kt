package com.example.musicplayercompose.ui.homeview.composablehome

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.musicplayercompose.R


@Composable
fun BottomBarActions(
    onSettingsClick: () -> Unit,
    onPlayFirstSongClick: () -> Unit,
    onPlayRandomSongClick: () -> Unit

) {
    BottomAppBar {
        IconButton(
            onClick = onSettingsClick,
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

