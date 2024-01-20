package com.example.brewexplorer.ui.theme.translater

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object trans {

    fun getTranslater( fetchValue: String): Flow<String>  = callbackFlow {
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

        englishGermanTranslator.translate(fetchValue)
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

}