package com.company.cookingrecipes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.cookingrecipes.database.RecipeEntity
import com.company.cookingrecipes.databinding.RecycleviewItemBinding
import com.company.cookingrecipes.datamodels.RecipeResponse

class RecycleAdapter(private val context: Context,
                     private val recipeList: ArrayList<RecipeEntity>
) : RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder>() {

    class RecycleViewHolder(itemBinding: RecycleviewItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val recipeImage = itemBinding.ivRecipe
        val recipeName = itemBinding.tvRecipeName
        val cardView = itemBinding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        return RecycleViewHolder(RecycleviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        Glide.with(context).load(recipeList[position].featuredImage).placeholder(R.drawable.image_not_available).into(holder.recipeImage)
        holder.recipeName.text = recipeList[position].title

        holder.cardView.setOnClickListener {
            val intent = Intent(context, RecipeActivity::class.java)
            intent.putExtra("title", recipeList[position].title)
            intent.putExtra("image", recipeList[position].featuredImage)
            intent.putExtra("ingredients", recipeList[position].ingredients)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}