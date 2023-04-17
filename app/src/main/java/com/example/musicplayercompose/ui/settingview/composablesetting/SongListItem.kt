package com.example.musicplayercompose.ui.settingview.composablesetting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song


@Composable
fun SongListItem(
    song: Song,
    onClick: (Song) -> Unit,
    onDeleteSong: (Song) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = song.albumArtUri),
            contentDescription = stringResource(R.string.album_art),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(song.title, modifier = Modifier.weight(1f))

        Checkbox(
            checked = song.isSelected,
            onCheckedChange = { isChecked ->
                onClick(song.copy(isSelected = isChecked))
            }
        )

        IconButton(onClick = { onDeleteSong(song) }) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_song_button_description)
            )
        }

    }
}