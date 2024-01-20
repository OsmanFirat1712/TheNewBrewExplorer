package com.example.brewexplorer.ui.theme.root

import androidx.compose.runtime.Composable
import com.example.brewexplorer.ui.theme.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost


@Composable
fun RootScreen() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}