package com.example.musicplayercompose.ui.settingview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.ui.settingview.composablesetting.SongListSetting
import com.example.musicplayercompose.ui.settingview.viewmodel.CustomViewModelFactory
import com.example.musicplayercompose.ui.settingview.viewmodel.SettingScreenViewModel
import com.example.musicplayercompose.utils.composeView

class SettingScreenFragment : Fragment() {
    private val viewModel: SettingScreenViewModel by activityViewModels {
        CustomViewModelFactory(SongRepository)
    }
    private lateinit var songs: MutableList<Song>
    private val songListState = mutableStateOf<List<Song>>(listOf())

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.addNewSongsToProvider(requireActivity().contentResolver, requireActivity().resources, ::loadSongsFromProvider)
        loadSongsFromProvider()
        return composeView {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            viewModel.addSelectedSongsToHomeScreen(
                                songListState,
                                requireActivity().contentResolver
                            )
                        },
                        content = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_song_button_description)
                            )
                        }
                    )
                },
                floatingActionButtonPosition = FabPosition.End,
            ) { contentPadding ->
                activity?.let {
                    SongListSetting(
                        activity = it,
                        songsState = songListState,
                        paddingValues = contentPadding,
                        viewModel = viewModel
                    )
                }
            }
        }
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
    }
}

