package com.example.brewexplorer.ui.theme.translater

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

object Translator {


    fun getTranslater(text: String) = callbackFlow {
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

        englishGermanTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                for (translate in translatedText){
                    trySend(translatedText) // Sendet das übersetzte Ergebnis.

                }
            }
            .addOnFailureListener { exception ->
                // Error handling.
                close(exception) // Schließt den Flow mit einem Fehler.
            }

        awaitClose {
            englishGermanTranslator.close() // Ressourcen freigeben.
        }

    }

}