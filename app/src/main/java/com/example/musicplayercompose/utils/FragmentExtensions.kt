package com.example.musicplayercompose.utils

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.musicplayercompose.ui.theme.MyApplicationTheme

inline fun Fragment.composeView(
    crossinline content: @Composable () -> Unit
): View {
    return ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MyApplicationTheme {
                content()
            }
        }
    }
}
