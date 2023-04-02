package com.company.cookingrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.company.cookingrecipes.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {

    private var binding: ActivityRecipeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarRecipe)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarRecipe?.setNavigationOnClickListener {
            onBackPressed()
        }

        Glide.with(this).load(intent.getStringExtra("image")).placeholder(R.drawable.image_not_available).into(binding?.ivRecipe!!)

        if (intent.hasExtra("title")) {
            binding?.tvRecipeName?.text = intent.getStringExtra("title")
        } else {
            binding?.tvRecipeName?.text = "No recipe available."
        }

        if (intent.hasExtra("ingredients")) {
            var ingredients = intent.getStringExtra("ingredients")!!.split("^")

            ingredients.forEach {
                binding?.tvRecipeIngredients?.append("- $it \n")
            }
        } else {
            binding?.tvRecipeIngredients?.text = "No ingredients available."
        }
    }
}