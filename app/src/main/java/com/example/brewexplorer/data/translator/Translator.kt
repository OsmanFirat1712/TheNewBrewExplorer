package com.example.brewexplorer.data.translator

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Translator object to handle text translation using Google ML Kit.
 * Provides functionality to translate text from English to German.
 */
object Translator {

    /**
     * Creates a coroutine flow to handle the translation process.
     * The function initiates the download of the translation model if needed,
     * and then performs the translation.
     *
     * @param text The text to be translated from English to German.
     * @return A coroutine flow that emits the translated text.
     */
    fun getTranslator(text: String) = callbackFlow {
        // Setting up the translator options for English to German translation.
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()

        // Getting the translation client with the specified options.
        val englishGermanTranslator = Translation.getClient(options)

        // Conditions to download the translation model (requires Wi-Fi).
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        // Attempting to download the translation model if needed.
        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Log.d("Translator", "downloaded translator model")
            }
            .addOnFailureListener { exception ->
                // Handling errors in model download.
                close(exception)
                Log.d("Translator", "couldn't download translator model ${exception}")
            }

        // Performing the translation.
        englishGermanTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                // Sending the translated text through the flow.
                trySend(translatedText)
            }
            .addOnFailureListener { exception ->
                // Handling errors in translation.
                close(exception)
                Log.d("Translator", "couldn't translate ${exception}")
            }

        // Closing the translator client and releasing resources when flow collection is stopped.
        awaitClose {
            englishGermanTranslator.close()
        }
    }
}
