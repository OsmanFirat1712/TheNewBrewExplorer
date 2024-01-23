package com.example.brewexplorer.ui.theme.base

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


data class Action(val icon: ImageVector, val contentDescri: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(title: String, navigationIcon: Action, actions: List<Action> = emptyList()) {
    TopAppBar(
        title = { Text(text = title) },

        navigationIcon = {
            IconButton(
                onClick = navigationIcon.onClick
            ) {
                Icon(
                    imageVector = navigationIcon.icon,
                    contentDescription = navigationIcon.contentDescri,
                )
            }
        },

        actions = {
            actions.forEach {
                IconButton(
                    onClick = it.onClick
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.contentDescri,
                    )
                }
            }
        },)
}