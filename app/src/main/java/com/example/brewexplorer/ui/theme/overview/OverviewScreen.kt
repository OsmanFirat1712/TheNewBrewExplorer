package com.example.brewexplorer.ui.theme.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brewexplorer.R
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.DataState
import com.example.brewexplorer.ui.theme.base.ContentEmptyScreen
import com.example.brewexplorer.ui.theme.base.ContentErrorScreen
import com.example.brewexplorer.ui.theme.base.ContentLoadingScreen
import com.example.brewexplorer.ui.theme.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun OverviewScreen(
    navigator: DestinationsNavigator,
    viewModel: OverviewViewModel = koinViewModel()
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModelState by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(text = stringResource(id = R.string.title_overview_screen)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        })

    {
        Column(
            modifier = Modifier.padding(it)
        ) {
            when (viewModelState.dataState) {
                DataState.ERROR -> ContentErrorScreen { viewModel.loadBeers() }
                DataState.LOADING -> ContentLoadingScreen()
                DataState.SUCCESS -> BeerList(items = viewModelState.beerList) { beer: Beer ->
                    navigator.navigate(DetailScreenDestination(beer))
                }
                DataState.EMPTY -> ContentEmptyScreen(
                    R.string.empty_feed
                )
                DataState.NONE -> {}
            }
        }
    }
}

@Composable
fun BeerList(
    items: List<Beer>,
    onClick: (Beer) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items.forEach {
            item {
                BeerListItem(beer = it)

                { beer: Beer ->
                    onClick(beer)
                }
            }
        }
    }
}

@Composable
fun BeerListItem(
    beer: Beer,
    onClick: (Beer) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick(beer)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        shape = RoundedCornerShape(20.dp)
    )

    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                // for Accessibility purposes
                contentDescription = stringResource(R.string.talkback_contentdescr_user_picture),
                contentScale = ContentScale.Fit,
                model = beer.imageUrl
            )
            Column {
                Text(
                    text = beer.name,
                    modifier = Modifier
                        .padding(
                            5.dp
                        )
                )
                Text(
                    text = beer.tagline,
                    modifier = Modifier
                        .padding(
                            5.dp
                        ),
                    fontSize = 12.sp
                )
            }
        }
    }
}


