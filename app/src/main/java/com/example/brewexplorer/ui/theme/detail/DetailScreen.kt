package com.example.brewexplorer.ui.theme.detail

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.brewexplorer.R
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.DataState
import com.example.brewexplorer.ui.theme.base.ContentEmptyScreen
import com.example.brewexplorer.ui.theme.base.ContentErrorScreen
import com.example.brewexplorer.ui.theme.base.ContentLoadingScreen
import com.example.brewexplorer.ui.theme.overview.BeerList
import com.example.brewexplorer.ui.theme.overview.BeerListItem
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun DetailScreen(beer: Beer) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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

            var name  = ""
            val malts: List<String> = beer.ingredients.malt.map { malt ->
                "${malt.name}: ${malt.amount.value} ${malt.amount.unit}"
            }

            val hops: List<String> = beer.ingredients.hops.map { hop ->
                "${hop.name}: ${hop.amount.value} ${hop.amount.unit}, verleiht folgende Note: ${hop.attribute}"
            }

            val ingredientsList: List<String> = malts + hops



            beer.ingredients.malt.forEach {
                name = it.name
            }

            val list = mutableListOf(
                "sdaaskjsajasld",
                "sdaaskjsajasld",

                "sdaaskjsajasld",
                "sdaaskjsajasld",

            )



            val initialItems = listOf(
                DetailItems("Beschreibung", beer.description, false,),
                DetailItems("Zutaten",  name, true, ingredientsList),
                DetailItems("Tipps & Tricks", beer.brewersTips ,false),
                DetailItems("Promilienrechner","Berechne schnell ab wievivel Bier du nicht mehr fahren darfs",false)
            )
            ContentList(items = initialItems)
        }
    }


}


@Composable
fun ContentList(
    items: List<DetailItems>,
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items.forEach {
            item {
                ExpandableCard(title = it.description, description = it.content, isBulletPoint = it.isBulletPoint, items = it.contentList )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCard(
    title: String,
    description: String,
    isBulletPoint: Boolean,
    items:List<String>?,
    titleFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    titleFontWeight: FontWeight = FontWeight.Bold,
    descriptionFontSize: TextUnit = MaterialTheme.typography.titleSmall.fontSize,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionMaxLines: Int = 10,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    padding: Dp = 12.dp
) {
    var expandedState by remember { mutableStateOf(false) }
    val viewModel:DetailScreenViewModel = koinViewModel()
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )


    Card(
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(5.dp),
        shape = shape,
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = title,
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
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
                var textState by remember { mutableStateOf("Hello") }

                when(isBulletPoint){
                    true -> BulletList(style = TextStyle.Default, items =  items!!)
                    false -> Column {
                        Text(
                            modifier = Modifier.clickable {

                            },
                            text = description,
                            fontSize = descriptionFontSize,
                            fontWeight = descriptionFontWeight,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Visible
                        )

                        ch.benlu.composeform.fields.TextField(
                            label = "Name",
                            form = viewModel.forum,
                            fieldState = viewModel.forum.name,
                        ).Field()

                        ch.benlu.composeform.fields.TextField(
                            label = "Last Name",
                            form = viewModel.forum,
                            fieldState = viewModel.forum.lastName
                        ).Field()
                    }
                }
            }
        }
    }
}


@Composable
fun makeBulletedList(items: List<String>): AnnotatedString {
    val bulletString = "\u2022\t\t"
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val bulletStringWidth = remember(textStyle, textMeasurer) {
        textMeasurer.measure(text = bulletString, style = textStyle).size.width
    }
    val restLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = restLine))

    return buildAnnotatedString {
        items.forEach { text ->
            withStyle(style = paragraphStyle) {
                append(bulletString)
                append(text)
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

data class DetailItems(
    val description: String,
    val content: String,
    val isBulletPoint: Boolean,
    val contentList: List<String>? = null
)



