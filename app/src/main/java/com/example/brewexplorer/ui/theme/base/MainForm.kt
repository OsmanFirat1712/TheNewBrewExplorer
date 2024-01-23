package com.example.brewexplorer.ui.theme.base

import android.content.res.loader.ResourcesProvider
import androidx.compose.runtime.mutableStateOf
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.FormField
import ch.benlu.composeform.validators.MinLengthValidator
import ch.benlu.composeform.validators.NotEmptyValidator

class MainForm(): Form() {
    override fun self(): Form {
        return this
    }

    @FormField
    val gender = FieldState(
        state = mutableStateOf<String?>(null)
    )


    @FormField
    val age = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            MinLengthValidator(
                minLength = 2,
                errorText = "Bitte gib deinen Alter ein "
            )
        )
    )

    @FormField
    val weight = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            MinLengthValidator(
                minLength = 2,
                errorText = "Bitte gib dein Gewicht ein "
            )
        )
    )

    @FormField
    val height = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            MinLengthValidator(
                minLength = 2,
                errorText = "Bitte gib deine Größe ein "
            )
        )
    )


    @FormField
    val countOfDrunkBeers = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf( MinLengthValidator(
            minLength = 1,
            errorText = "Bitte gib die Anzahl von den getrunkenen Bieren an "
        ))
    )

}