package com.example.brewexplorer.ui.theme.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewexplorer.data.BeerDataSource
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.DataState
import com.example.brewexplorer.data.remote.model.RepositoryResponse
import com.example.brewexplorer.ui.theme.translater.Translator
import com.example.brewexplorer.ui.theme.translater.Transle
import com.example.brewexplorer.ui.theme.translater.test
import com.example.brewexplorer.ui.theme.translater.trans
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OverviewViewModel(val dataSource: BeerDataSource): ViewModel() {
    val translateQueue = MutableSharedFlow<String>(replay = 1)

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    var transled = "haha"
    val flow = Translator.getTranslater (_state.value.translated ).onEach {
        transled = it
        println("testmus $it")
    }
    val translate = callbackFlow {

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()

        val englishGermanTranslator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                // Model downloaded successfully.
            }
            .addOnFailureListener { exception ->
                // Error handling.
                close(exception) // Schließt den Flow mit einem Fehler.
            }

        englishGermanTranslator.translate(_state.value.translated)
            .addOnSuccessListener { translatedText ->
                trySend(translatedText) // Sendet das übersetzte Ergebnis.
            }
            .addOnFailureListener { exception ->
                // Error handling.
                close(exception) // Schließt den Flow mit einem Fehler.
            }

        awaitClose {
            englishGermanTranslator.close() // Ressourcen freigeben.
        }

    }


    data class State(
        val beerList: List<Beer> = emptyList(),
        val dataState: DataState = DataState.NONE,
        var translated:String = "",
        val test:String = "",
        val beer:Beer? = null
    )

    init {

        viewModelScope.launch() {

            _state.update { it.copy(dataState = DataState.LOADING) }
            dataSource.getBeerList().let { response ->
                when (response) {
                    is RepositoryResponse.Error -> _state.update { it.copy(dataState = DataState.ERROR) }
                    is RepositoryResponse.Success -> _state.update {

                        when (response.data.isNotEmpty()) {
                            true -> it.copy(
                                beerList = response.data,
                                dataState = DataState.SUCCESS,
                            )

                            false -> it.copy(
                                dataState = DataState.EMPTY
                            )
                        }
                    }
                }
            }


            state.value.beerList.forEach { beer ->
                Translator.getTranslater(beer.tagline).onEach { translatedText ->
                        val updatedBeerList = state.value.beerList.map { currentBeer ->
                            if (currentBeer.id == beer.id) {
                                currentBeer.copy(tagline = translatedText)
                            } else {
                                currentBeer
                            }
                        }
                        _state.emit(state.value.copy(beerList = updatedBeerList, dataState = DataState.SUCCESS))

                }.launchIn(this)
            }
        }

    }

    private fun getTranslatedText(beerList: List<Beer>){
    viewModelScope.launch {

        state.value.beerList.forEach { beer ->
            Translator.getTranslater(beer.tagline).onEach { translatedText ->
                viewModelScope.launch {
                    val updatedBeerList = state.value.beerList.map { currentBeer ->
                        if (currentBeer.id == beer.id) {
                            currentBeer.copy(tagline = translatedText)
                        } else {
                            currentBeer
                        }
                    }
                    _state.emit(state.value.copy(beerList = updatedBeerList, dataState = DataState.SUCCESS))
                }
            }.launchIn(this)
        }
    }
    }

}



fun OverviewViewModel.createCallBack(fetchValue: () -> String): Flow<String> = callbackFlow {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.GERMAN)
        .build()

    val englishGermanTranslator = Translation.getClient(options)

    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    englishGermanTranslator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {
            // Model downloaded successfully.
        }
        .addOnFailureListener { exception ->
            // Error handling.
            close(exception) // Schließt den Flow mit einem Fehler.
        }

    englishGermanTranslator.translate(fetchValue())
        .addOnSuccessListener { translatedText ->
            trySend(translatedText) // Sendet das übersetzte Ergebnis.
        }
        .addOnFailureListener { exception ->
            // Error handling.
            close(exception) // Schließt den Flow mit einem Fehler.
        }

    awaitClose {
        englishGermanTranslator.close() // Ressourcen freigeben.
    }

}


