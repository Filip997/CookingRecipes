package com.company.cookingrecipes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe-table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String,
    val title: String,
    val featuredImage: String,
    val ingredients: String,
    val page: Int
)