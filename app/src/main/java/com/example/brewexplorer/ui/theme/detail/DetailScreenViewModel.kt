package com.example.brewexplorer.ui.theme.detail

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewexplorer.ui.theme.base.MainForm
import com.example.brewexplorer.ui.theme.navArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailScreenViewModel(val forum: MainForm, private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    val navArgsSavedBeerItem: DetailScreenNavArgs = savedStateHandle.navArgs()

    val initialItems = listOf(
        DetailScreenItems(
            navArgsSavedBeerItem.beerItem.name,
            "Beschreibung",
            ScreenState.DEFAULT,
            null,
            navArgsSavedBeerItem.beerItem.description
        ),
        DetailScreenItems(
            navArgsSavedBeerItem.beerItem.name,
            "Zutaten",
            ScreenState.BULLETPOINT,
            navArgsSavedBeerItem.beerItem.combinedMalts
        ),
        DetailScreenItems(
            navArgsSavedBeerItem.beerItem.name,
            "Tipps & Tricks",
            ScreenState.DEFAULT,
            null,
            navArgsSavedBeerItem.beerItem.brewersTips
        ),
        DetailScreenItems(
            navArgsSavedBeerItem.beerItem.name,
            "Promilienrechner",
            ScreenState.PROMILCALCULATOR,
            null,
            "Berechne schnell ab wieviel Bier du nicht mehr fahren darfst"
        )
    )

    data class State(
        val beerItem: List<DetailScreenItems> = emptyList(),
        val screenState: ScreenState = ScreenState.DEFAULT,
        val currentBAK: Double = 0.0,
        val hoursToSober: Int = 0
    )
    sealed class Action {
        data class Calculate(val gender: String) : Action()
    }

    init {
        viewModelScope.launch {
            val genderFlow = snapshotFlow { forum.gender.state.value }
            val ageFlow = snapshotFlow { forum.age.state.value }
            val weight = snapshotFlow { forum.weight.state.value }
            val totalBeersFlow = snapshotFlow { forum.countOfDrunkBeers.state.value }

            combine(
                genderFlow,
                ageFlow,
                weight,
                totalBeersFlow
            ) { gender, age, weight, totalbeers ->
                validate()
                if (forum.isValid) {
                    gender?.let {
                        calculateBAK(it)
                    }
                }
            }
                .distinctUntilChanged()
                .debounce(1000)
                .launchIn(this)

            _state.update {
                it.copy(
                    beerItem = initialItems
                )
            }
        }
    }
    fun validate(): Boolean {
        val test = forum.validate(true)
        Log.d("MainViewModel", "Validate (form is valid: ${forum.isValid})")
        return forum.isValid
    }

    /**
     * Calculates the Blood Alcohol Content (BAC) based on user inputs.
     * This method takes into account the user's gender, height, weight,
     * number of drinks consumed, and the alcohol by volume (ABV) of the drinks.
     *
     * @param gender The gender of the user, used to calculate the reduction factor.
     *               Expected values are "Male" or "Female".
     */
    fun calculateBAK(gender: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                // Number of drinks consumed, retrieved from the user input.
                val numberOfDrinks = forum.countOfDrunkBeers.state.value?.toInt() ?: 0

                // Height of the user in centimeters, retrieved from the user input.
                val height = forum.height.state.value?.toDouble() ?: 0.0

                // Weight of the user in kilograms, retrieved from the user input.
                val weight = forum.weight.state.value?.toDouble() ?: 0.0

                // Alcohol by volume (ABV) of the beer, expressed as a decimal.
                // For example, 5% ABV is represented as 0.05.
                val abv = navArgsSavedBeerItem.beerItem.abv / 100.0

                // The volume of one beer in liters. In this case, 0.5 liters per beer is assumed.
                val beerVolumeLiters = 0.5

                // Calculating the total grams of alcohol per beer.
                val alcoholGramsPerBeer = beerVolumeLiters * abv * 0.789 * 1000

                // Reduction factor calculation based on gender, height, and weight.
                val reductionFactor = if (gender == "Male") {
                    0.31608 - 0.004821 * weight + 0.004432 * height
                } else {
                    0.31223 - 0.006446 * weight + 0.004466 * height
                }

                // Calculating the total grams of alcohol consumed.
                val totalAlcoholGrams = alcoholGramsPerBeer * numberOfDrinks

                // Calculating the raw BAC value.
                val currentBAKRaw = totalAlcoholGrams / (weight * reductionFactor)

                // Time to sober up, estimated based on BAC.
                val hoursToSoberRaw = if (currentBAKRaw > 0) currentBAKRaw / 0.15 else 0.0

                // Formatting the BAC and hours to sober to two decimal points.
                val currentBAK = "%.2f".format(currentBAKRaw).toDouble()
                val hoursToSober = "%.2f".format(hoursToSoberRaw).toDouble()

                // Updating the state with the calculated BAC and hours to sober.
                currentState.copy(currentBAK = currentBAK, hoursToSober = hoursToSober.toInt())
            }
        }
    }


    fun execute(action: Action) {
        when (action) {
            is Action.Calculate -> calculateBAK(action.gender)
        }
    }

    fun resetState() {
        forum.age.state.value = ""
        forum.height.state.value = ""
        forum.gender.state.value = ""
        forum.weight.state.value = ""
    }

    override fun onCleared() {
        resetState()
        super.onCleared()
    }
}

enum class ScreenState {
    BULLETPOINT, PROMILCALCULATOR, DEFAULT,
}