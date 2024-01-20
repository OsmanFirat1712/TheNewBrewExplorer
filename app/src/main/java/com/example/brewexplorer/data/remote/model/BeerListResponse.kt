package com.example.brewexplorer.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable;

data class Beer(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName ("image_url")
    val imageUrl: String,
    val abv: Double,
    var tagline: String,
    @SerializedName ("brewers_tips")
    val brewersTips: String,
    val ingredients: Ingredients,
    @SerializedName ("boil_volume")
    val boilVolume: Volume,
    ):Serializable


data class Ingredients(
    val malt: List<Malt>,
    val hops: List<Hop>,
    val yeast: String
):Serializable

data class Malt(
    val name: String,
    val amount: Amount
):Serializable

data class Hop(
    val name: String,
    val amount: Amount,
    val add: String,
    val attribute: String
):Serializable

data class Amount(
    val value: Double,
    val unit: String
):Serializable


data class Volume(
    val value: Int,
    val unit: String
):Serializable