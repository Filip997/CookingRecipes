package com.company.cookingrecipes

import com.company.cookingrecipes.datamodels.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RecipeService {

    @GET("recipe/search")
    fun getRecipe(
        @Query("page") page: Int,
        @Query("query") query: String,
        @Header("Authorization") auth: String
    ) : Call<RecipeResponse>
}