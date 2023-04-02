package com.company.cookingrecipes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [RecipeEntity::class], version = 1)
abstract class RecipeDatabase : RoomDatabase(){

    abstract fun recipeDao(): RecipeDao

    companion object {

        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context): RecipeDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe-database"
                    ).fallbackToDestructiveMigration().build()
                }

                return instance
            }
        }
    }
}