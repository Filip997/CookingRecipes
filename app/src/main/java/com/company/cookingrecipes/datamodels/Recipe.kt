package com.company.cookingrecipes.datamodels

data class Recipe(
    val pk: Int,
    val title: String,
    val publisher: String,
    val featured_image: String,
    val rating: Int,
    val source_url: String,
    val description: String?,
    val cooking_instructions: String?,
    val ingredients: List<String>,
    val date_added: String,
    val date_updated: String,
    val long_date_added: Long,
    val long_date_updated: Long
)