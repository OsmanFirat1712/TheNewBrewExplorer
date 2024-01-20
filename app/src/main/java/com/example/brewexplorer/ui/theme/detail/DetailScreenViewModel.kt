package com.example.brewexplorer.ui.theme.detail

import android.content.res.loader.ResourcesProvider
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.ui.theme.base.MainForm

class DetailScreenViewModel(val forum:MainForm,    private val savedStateHandle: SavedStateHandle):ViewModel() {

    init {

    }

    fun validate() {
        forum.validate(true)
        Log.d("MainViewModel", "Validate (form is valid: ${forum.isValid})")
    }
}




enum class ScreenState {
    BULLETPOINTLIST, NORMAL, PROMILCALCULAR
}


data class MessagesScreenNavArgs(
    val id: String,
)