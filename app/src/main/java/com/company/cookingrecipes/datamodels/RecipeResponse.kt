package com.company.cookingrecipes.datamodels

data class RecipeResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Recipe>
)