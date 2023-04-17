package com.example.musicplayercompose.ui.homeview.model

data class PullRefreshState(
    val isRefreshing: Boolean,
    val onRefresh: () -> Unit
)