package com.example.brewexplorer.ui.theme.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewexplorer.data.BeerDataSource
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.DataState
import com.example.brewexplorer.data.remote.model.RepositoryResponse
import com.example.brewexplorer.data.translator.Translator
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the overview screen, managing the state and data for a list of beers.
 *
 * @property dataSource Source of beer data.
 */
class OverviewViewModel(val dataSource: BeerDataSource): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private var malts: List<String> = emptyList()
    private var hops: List<String> = emptyList()

    /**
     * Represents the state of the overview screen.
     *
     * @property beerList The list of beers to display.
     * @property dataState The current state of data (loading, success, error).
     */
    data class State(
        val beerList: List<Beer> = emptyList(),
        val dataState: DataState = DataState.NONE,
    )

    /**
     * Initialization block for the ViewModel.
     * Retrieves a list of beers from the data source and updates the state accordingly.
     */
    init {
        loadBeers()
    }

    fun loadBeers() {
        viewModelScope.launch {
            _state.update { it.copy(dataState = DataState.LOADING) }
            dataSource.getBeerList().let { response ->
                when (response) {
                    is RepositoryResponse.Error -> _state.update { it.copy(dataState = DataState.ERROR) }
                    is RepositoryResponse.Success -> _state.update {
                        val updatedBeers = response.data.map { beer ->
                            translateBeerData(beer)
                        }
                        it.copy(beerList = updatedBeers, dataState = DataState.SUCCESS)
                    }
                }
            }
        }
    }


    /**
     * Translates the text using the Translator.
     *
     * @param text The text to be translated.
     * @return The translated text.
     */
    private suspend fun translateText(text: String): String {
        return Translator.getTranslator(text).firstOrNull() ?: text
    }

    /**
     * Translates a list of ingredients.
     *
     * @param ingredientsList The list of ingredients to translate.
     * @return The list of translated ingredients.
     */
    private suspend fun translateIngredients(ingredientsList: List<String>): List<String> {
        return ingredientsList.map { ingredient ->
            translateText(ingredient)
        }
    }

    /**
     * Translates beer data including tagline, description, brewers tips, and ingredients.
     *
     * @param beer The beer data to translate.
     * @return The beer data with translated fields.
     */
    private suspend fun translateBeerData(beer: Beer): Beer = withContext(viewModelScope.coroutineContext) {
        val translatedTagline = async { translateText(beer.tagline) }
        val translatedDescription = async { translateText(beer.description) }
        val translatedBrewerTips = async { translateText(beer.brewersTips) }
        val ingredientsList: List<String> = malts + hops
        val translatedIngredients = async { translateIngredients(ingredientsList) }

        beer.copy(
            tagline = translatedTagline.await(),
            description = translatedDescription.await(),
            brewersTips = translatedBrewerTips.await(),
            combinedMalts = translatedIngredients.await()
        )
    }

}

