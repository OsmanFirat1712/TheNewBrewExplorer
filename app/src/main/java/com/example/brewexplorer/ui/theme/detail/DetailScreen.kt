package com.example.brewexplorer.ui.theme.detail

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.benlu.composeform.FieldState
import com.example.brewexplorer.R
import com.example.brewexplorer.ui.theme.base.MainForm
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Destination(navArgsDelegate = DetailScreenNavArgs::class)
@Composable
fun DetailScreen( navigator: DestinationsNavigator) {
    val viewModel: DetailScreenViewModel = koinViewModel()
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = viewModelState.beerItem.first().beerName) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

    )

    { it ->
        Column(
            modifier = Modifier.padding(it)
        ) {

            ContentList(
                items = viewModelState.beerItem,
                result = viewModelState.currentBAK,
                hoursToSober = viewModelState.hoursToSober.toInt(),
                forum = viewModel.forum,
                calculate = {
                    if (it != null) {
                        viewModel.execute(DetailScreenViewModel.Action.Calculate(it))
                    }
                }
            )
        }
    }
}


@Composable
fun ContentList(
    items: List<DetailScreenItems>,
    result: Double?,
    hoursToSober: Int?,
    forum:MainForm,
    calculate: (String?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items.forEach {
            item {
                ExpandableCardd(
                    it,
                    result = result,
                    hoursToSober = hoursToSober,
                    beerName = it.beerName,
                    forum = forum,
                    calculate = calculate
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCardd(
    detailScreenItems: DetailScreenItems,
    result: Double?,
    hoursToSober: Int?,
    beerName: String,
    forum:MainForm,
    calculate: (String?) -> Unit,
    descriptionFontSize: TextUnit = MaterialTheme.typography.titleSmall.fontSize,
    descriptionMaxLines: Int = 10,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    val radioOptions = listOf("Männlich", "Weiblich")
    forum.gender.state.value = "Männlich"
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }

    Card(
        modifier = Modifier
            .clickable(!expandedState, onClick = {
                expandedState = !expandedState
            }
            )
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = detailScreenItems.description,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(0.2f)
                        .rotate(rotationState),
                    onClick = {
                            expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {

                when (detailScreenItems.screenState) {

                    ScreenState.DEFAULT -> Text(
                        modifier = Modifier.clickable {

                        },
                        text = detailScreenItems.content!!,
                        fontSize = descriptionFontSize,
                        fontWeight = FontWeight.Normal,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Visible
                    )

                    ScreenState.BULLETPOINT -> BulletList(
                        style = TextStyle.Default,
                        items = detailScreenItems.contentList!!
                    )

                    ScreenState.PROMILCALCULATOR -> Column {
                        Text(
                            text = detailScreenItems.description,
                            fontSize = descriptionFontSize,
                            fontWeight = FontWeight.Normal,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Visible
                        )

                        radioOptions.forEach { gender ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (gender == selectedOption),
                                    onClick = { selectedOption = gender
                                        forum.gender.state.value = gender
                                    }
                                )
                                Text(
                                    text = gender,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        ch.benlu.composeform.fields.TextField(
                            label = "Alter",
                            form = forum,
                            keyboardType = KeyboardType.Number,
                            fieldState = forum.age,
                        ).Field()

                        ch.benlu.composeform.fields.TextField(
                            label = "Gewicht",
                            keyboardType = KeyboardType.Number,
                            form = forum,
                            fieldState = forum.weight
                        ).Field()

                        ch.benlu.composeform.fields.TextField(
                            label = "Größe",
                            keyboardType = KeyboardType.Number,
                            form = forum,
                            fieldState = forum.height
                        ).Field()


                        ch.benlu.composeform.fields.TextField(
                            label = "Anzahl der Biere",
                            keyboardType = KeyboardType.Number,
                            form = forum,
                            fieldState = forum.countOfDrunkBeers
                        ).Field()

                        when(forum.isValid){
                            true ->  {
                                Text(text = "Du hast nach ${forum.countOfDrunkBeers.state.value} $beerName einen Promilien Wert von $result und bist nach etwa $hoursToSober Stunden nüchtern!")
                            }
                            false -> {}
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun BulletList(
    modifier: Modifier = Modifier,
    style: TextStyle,
    indent: Dp = 20.dp,
    lineSpacing: Dp = 0.dp,
    items: List<String>,
) {
    Column(modifier = modifier) {
        items.forEach {
            Row {
                Text(
                    text = "\u2022",
                    style = style.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.width(indent),
                )
                Text(
                    text = it,
                    style = style,
                    modifier = Modifier.weight(1f, fill = true),
                )
            }
            if (lineSpacing > 0.dp && it != items.last()) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}