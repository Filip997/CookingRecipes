package com.company.cookingrecipes

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.cookingrecipes.database.RecipeApp
import com.company.cookingrecipes.database.RecipeDao
import com.company.cookingrecipes.database.RecipeEntity
import com.company.cookingrecipes.databinding.ActivityMainBinding
import com.company.cookingrecipes.databinding.DialogInformationBinding
import com.company.cookingrecipes.datamodels.RecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private var recipeResponse: RecipeResponse? = null
    private var page = 0
    private var queryRecipe = "meat"
    private var viaInternet = true  //Shows if the last search was via internet connection or from database

    private var dialogProgress: Dialog? = null

    private lateinit var recycleAdapter: RecycleAdapter

    private lateinit var recipeDao: RecipeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarMainActivity)

        recipeDao = (application as RecipeApp).db.recipeDao()

        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                if (text != null && text.trim().isNotEmpty()) {
                    queryRecipe = text
                    page = 1

                    if (Constants.isNetworkAvailable(this@MainActivity)) {
                        viaInternet = true
                        setupDataFromInternetConnection()
                    } else {
                        viaInternet = false
                        setupDataFromDatabase()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding?.btnMeat?.setOnClickListener {
            queryRecipe = "meat"
            binding?.searchView?.setQuery(queryRecipe, false)
            page = 1

            if (Constants.isNetworkAvailable(this@MainActivity)) {
                viaInternet = true
                setupDataFromInternetConnection()
            } else {
                viaInternet = false
                setupDataFromDatabase()
            }
        }

        binding?.btnSalad?.setOnClickListener {
            queryRecipe = "salad"
            binding?.searchView?.setQuery(queryRecipe, false)
            page = 1

            if (Constants.isNetworkAvailable(this@MainActivity)) {
                viaInternet = true
                setupDataFromInternetConnection()
            } else {
                viaInternet = false
                setupDataFromDatabase()
            }
        }

        binding?.btnSoup?.setOnClickListener {
            queryRecipe = "soup"
            binding?.searchView?.setQuery(queryRecipe, false)
            page = 1

            if (Constants.isNetworkAvailable(this@MainActivity)) {
                viaInternet = true
                setupDataFromInternetConnection()
            } else {
                viaInternet = false
                setupDataFromDatabase()
            }
        }

        binding?.btnVegetables?.setOnClickListener {
            queryRecipe = "vegetables"
            binding?.searchView?.setQuery(queryRecipe, false)
            page = 1

            if (Constants.isNetworkAvailable(this@MainActivity)) {
                viaInternet = true
                setupDataFromInternetConnection()
            } else {
                viaInternet = false
                setupDataFromDatabase()
            }
        }

        binding?.btnFruit?.setOnClickListener {
            queryRecipe = "fruit"
            binding?.searchView?.setQuery(queryRecipe, false)
            page = 1

            if (Constants.isNetworkAvailable(this@MainActivity)) {
                viaInternet = true
                setupDataFromInternetConnection()
            } else {
                viaInternet = false
                setupDataFromDatabase()
            }
        }

        binding?.btnNext?.setOnClickListener {
            if (page != 0) {
                if (Constants.isNetworkAvailable(this@MainActivity)) {
                    if (viaInternet) {
                        if (recipeResponse != null) {
                            if (recipeResponse?.next != null) {
                                page++
                                viaInternet = true
                                setupDataFromInternetConnection()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "This is the last page",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        showInformationDialog("Internet connection is on again. Search the same category from start.")
                    }
                } else {
                    if (!viaInternet) {
                        if (binding?.tvNoRecipes?.visibility != View.VISIBLE) {
                            page++
                            viaInternet = false
                            setupDataFromDatabase()
                        }
                    } else {
                        showInformationDialog(
                            "Internet connection is off. Search the same category from start to find if there are any saved recipes."
                        )
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "First search for a recipe!", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnPrevious?.setOnClickListener {
            if (page != 0) {
                if (page > 1) {
                    if (Constants.isNetworkAvailable(this@MainActivity)) {
                        if (viaInternet) {
                            if (recipeResponse != null) {
                                if (recipeResponse?.previous != null) {
                                    page--
                                    viaInternet = true
                                    setupDataFromInternetConnection()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "You're at the first page!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            showInformationDialog(
                                "Internet connection is on again. Search the same category from start."
                            )
                        }
                    } else {
                        if (!viaInternet) {
                            page--
                            viaInternet = false
                            setupDataFromDatabase()
                        } else {
                            showInformationDialog(
                                "Internet connection is off. Search the same category from start to find if there are any saved recipes."
                            )
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "You're at the first page!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "First search for a recipe!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDataFromInternetConnection() {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

        val service: RecipeService = retrofit.create<RecipeService>(RecipeService::class.java)

        val listCall: Call<RecipeResponse> = service.getRecipe(
            page, queryRecipe, Constants.AUTHORIZATION_TOKEN
        )

        showProgressDialog()
        listCall.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (response.isSuccessful) {
                    recipeResponse = response.body()

                    hideProgressDialog()
                    if (recipeResponse != null) {

                        binding?.tvNoRecipes?.visibility = View.GONE
                        binding?.recycleView?.visibility = View.VISIBLE

                        if (recipeResponse!!.count > 0) {

                            val listRecipes = ArrayList<RecipeEntity>()
                            for (i in 0 until recipeResponse!!.results.size) {

                                val ingredients = getIngredients(recipeResponse?.results!![i].ingredients)

                                listRecipes.add(RecipeEntity(
                                    query = queryRecipe,
                                    title = recipeResponse!!.results[i].title,
                                    featuredImage = recipeResponse!!.results[i].featured_image,
                                    ingredients = ingredients,
                                    page = page
                                ))
                            }

                            recycleAdapter = RecycleAdapter(this@MainActivity, listRecipes)
                            binding?.recycleView?.layoutManager = LinearLayoutManager(this@MainActivity)
                            binding?.recycleView?.adapter = recycleAdapter

                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    if (recipeDao.getNumOfRecipesByCategoryAndPage(queryRecipe, page) > 0) {
                                        recipeDao.deleteAllRecipesByCategoryAndPage(queryRecipe, page)
                                    }

                                    for (i in 0 until listRecipes.size) {
                                        recipeDao.insert(listRecipes[i])
                                    }
                                }
                            }
                        } else {
                            page = 0
                            binding?.tvNoRecipes?.text = "There aren't any recipes available by this category"
                            binding?.recycleView?.visibility = View.GONE
                            binding?.tvNoRecipes?.visibility = View.VISIBLE
                        }
                    } else {
                        page = 0
                        binding?.tvNoRecipes?.text = "Something went wrong"
                        binding?.recycleView?.visibility = View.GONE
                        binding?.tvNoRecipes?.visibility = View.VISIBLE
                    }

                } else {
                    val rc = response.code()
                    when(rc) {
                        400 -> {
                            showAlertDialog("Error 400", "Bad Connection")
                        }
                        404 -> {
                            showAlertDialog("Error 404", "Not Found")
                        }
                        else -> {
                            showAlertDialog("Error", "Something went wrong")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                showAlertDialog("Error", t.message.toString())
            }

        })
    }

    private fun getIngredients(list: List<String>): String {
        val ingredients = StringBuilder()
        for (ingredient in list) {
            ingredients.append("$ingredient^")
        }
        ingredients.deleteCharAt(ingredients.length - 1)

        return ingredients.toString()
    }

    private fun setupDataFromDatabase() {
        lifecycleScope.launch {
            showProgressDialog()

            if (recipeDao.getNumOfRecipesByCategory(queryRecipe) > 0) {
                if (recipeDao.getNumOfRecipesByCategoryAndPage(queryRecipe, page) > 0) {
                    binding?.tvNoRecipes?.visibility = View.GONE
                    binding?.recycleView?.visibility = View.VISIBLE

                    val listRecipes = ArrayList<RecipeEntity>(
                        recipeDao.getAllRecipesByCategoryAndPage(queryRecipe, page)
                    )

                    hideProgressDialog()

                    recycleAdapter = RecycleAdapter(this@MainActivity, listRecipes)
                    binding?.recycleView?.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding?.recycleView?.adapter = recycleAdapter

                } else {
                    hideProgressDialog()

                    binding?.tvNoRecipes?.text =
                        "There aren't any more recipes saved of this category. Turn on the internet connection and find more."
                    binding?.recycleView?.visibility = View.GONE
                    binding?.tvNoRecipes?.visibility = View.VISIBLE
                }
            } else {
                hideProgressDialog()

                page = 0
                binding?.tvNoRecipes?.text = "There aren't any recipes saved of this category. Turn on the internet connection and search again."
                binding?.recycleView?.visibility = View.GONE
                binding?.tvNoRecipes?.visibility = View.VISIBLE
            }
        }
    }

    private fun showProgressDialog() {
        dialogProgress = Dialog(this@MainActivity)
        dialogProgress?.setContentView(R.layout.dialog_progress)
        dialogProgress?.setCancelable(false)
        dialogProgress?.show()
    }

    private fun hideProgressDialog() {
        if (dialogProgress != null) {
            dialogProgress?.dismiss()
            dialogProgress = null
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }

    private fun showInformationDialog(message: String) {
        val customDialog = Dialog(this)
        val dialogBinding = DialogInformationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(customDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.tvMessage.text = message
        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.window?.attributes = lp

        customDialog.show()
    }
}