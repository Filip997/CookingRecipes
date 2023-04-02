package com.company.cookingrecipes.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insert(recipeEntity: RecipeEntity)

    @Update
    suspend fun update(recipeEntity: RecipeEntity)

    @Delete
    suspend fun delete(recipeEntity: RecipeEntity)

    @Query("SELECT * FROM `recipe-table` WHERE `query`=:category AND `page`=:page")
    suspend fun getAllRecipesByCategoryAndPage(category: String, page: Int): List<RecipeEntity>

    @Query("DELETE FROM `recipe-table` WHERE `query`=:category AND `page`=:page")
    suspend fun deleteAllRecipesByCategoryAndPage(category: String, page: Int)

    @Query("SELECT COUNT(*) FROM `recipe-table` WHERE `query`=:category AND `page`=:page")
    suspend fun getNumOfRecipesByCategoryAndPage(category: String, page: Int): Int

    @Query("SELECT COUNT(*) FROM `recipe-table` WHERE `query`=:category")
    suspend fun getNumOfRecipesByCategory(category: String): Int
}