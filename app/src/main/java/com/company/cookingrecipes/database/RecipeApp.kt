package com.company.cookingrecipes.database

import android.app.Application

class RecipeApp : Application() {
    val db by lazy {
        RecipeDatabase.getInstance(this)
    }
}