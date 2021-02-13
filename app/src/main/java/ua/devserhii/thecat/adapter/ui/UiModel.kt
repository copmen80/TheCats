package ua.devserhii.thecat.adapter.ui

/**
 * Created by Serhii Boiko on 24.01.2021.
 */
sealed class UiModel
data class CatUiModel(val url: String, val id: String) : UiModel()
