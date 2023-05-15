package com.abdurashidov.musicplayer.models

data class Music(
    val id: Long,
    val title: String,
    val imagePath: String,
    val musicPath: String,
    val author: String
)