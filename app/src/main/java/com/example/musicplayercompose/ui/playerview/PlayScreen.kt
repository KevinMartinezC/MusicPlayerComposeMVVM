package com.example.musicplayercompose.ui.playerview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.musicplayercompose.R
import com.example.musicplayercompose.ui.playerview.viewmodel.PlayScreenViewModel

@Composable
fun PlayScreen(viewModel: PlayScreenViewModel) {

 ScreenContentPlayer(viewModel.uiState)
}

@Composable
fun ScreenContentPlayer(uiState: PlayerUIState){
    val title by uiState.sonTitle.collectAsState()
    val imageSong by uiState.image.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(Modifier.padding(16.dp)) {
            Column {
                Image(
                    painter = imageSong?.let { rememberImagePainter(data = it) } ?: painterResource(id = R.drawable.album_art_1),
                    contentDescription = "Music Image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp)
                )

                Text(
                    text = title ?: "Song Title",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
                Slider(
                    value = 0f, // You should replace this with a state variable linked to your ViewModel
                    onValueChange = { /* Handle value change */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(onClick = { /* Handle previous button click */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_skip_previous_24),
                            contentDescription = "Previous Song Button"
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(onClick = { /* Handle play/pause button click */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_play_arrow_24),
                            contentDescription = "Play or Pause Song Button"
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(onClick = { /* Handle next button click */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_skip_next_24),
                            contentDescription = "Next Song Button"
                        )
                    }
                }
            }
        }
    }
}