package com.example.brewexplorer.ui.theme.detail

data class DetailScreenItems(
    val beerName: String,
    val description: String,
    val screenState: ScreenState,
    val contentList: List<String>? = null,
    val content: String? = null,
    )