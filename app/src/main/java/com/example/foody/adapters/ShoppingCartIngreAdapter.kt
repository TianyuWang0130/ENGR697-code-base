package com.example.foody.adapters

import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.data.database.entities.ShoppingCartEntity
import com.example.foody.data.database.entities.ShoppingCartIngreEntity
import com.example.foody.data.model.IngredientsSettlementInfo
import com.example.foody.databinding.ShoppingCartIngredientsRowLayoutBinding
import com.example.foody.ui.MainActivity
import com.example.foody.util.RecipesDiffUtil
import com.example.foody.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException
import java.util.*


class ShoppingCartIngreAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<ShoppingCartIngreAdapter.MyViewHolder>(), ActionMode.Callback{

    private var multiSelection = false

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var selectedRecipes = arrayListOf<ShoppingCartIngreEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    private var shoppingCartRecipes = emptyList<ShoppingCartIngreEntity>()

    class MyViewHolder(val binding: ShoppingCartIngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredientsSettlementInfo: IngredientsSettlementInfo) {
            binding.data = ingredientsSettlementInfo
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ShoppingCartIngredientsRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    private var ingredientsSettlementList = mutableListOf<IngredientsSettlementInfo>()
    fun getRecipesInfo(shoppingCartIngreEntity: ShoppingCartIngreEntity):IngredientsSettlementInfo{

        ingredientsSettlementList.forEach {
            if (it.shoppingCartIngre.id==shoppingCartIngreEntity.id){
                return it
            }
        }
        val carSettlementChecklist = IngredientsSettlementInfo(shoppingCartIngreEntity)
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url("https://api.kroger.com/v1/products?filter.term=snails")
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${MainActivity.token}")
            .build()
        Log.i("response", "access_token:${MainActivity.token} " )
            client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                // Get API access_token
                Log.i("response", "OnResponse: ${response.body()?.string()}"  )
                Log.i("response", response.body().toString() )
            }
        })
        ingredientsSettlementList.add(carSettlementChecklist)
        return carSettlementChecklist
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = shoppingCartRecipes[position]
        holder.bind(getRecipesInfo(currentRecipe))
        saveItemStateOnScroll(currentRecipe, holder)

        /**
         * Single Click Listener
         * */
        holder.binding.shoppingCartIngreRowLayout.setOnClickListener {
            if (multiSelection) {
                applySelection(holder, currentRecipe)
            } else {
//                val action =
//                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
//                        currentRecipe.result
//                    )
//                holder.itemView.findNavController().navigate(action)
            }
        }

        /**
         * Long Click Listener
         * */
        holder.binding.shoppingCartIngreRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipe)
                true
            } else {
                applySelection(holder, currentRecipe)
                true
            }

        }

        holder.binding.shoppingCartItemAdd.setOnClickListener {
            mainViewModel.insertShoppingCartIngre(currentRecipe)
        }

        holder.binding.shoppingCartItemReduce.setOnClickListener {
            mainViewModel.reduceShoppingCartIngre(currentRecipe)
        }

    }

    private fun saveItemStateOnScroll(currentRecipe: ShoppingCartIngreEntity, holder: MyViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: ShoppingCartIngreEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.shoppingCartIngreRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.ingredientsCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun getItemCount(): Int {
        return shoppingCartRecipes.size
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_recipe_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteShoppingCartIngreEntity(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")

            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newShoppingCartIngreEntities: List<ShoppingCartIngreEntity>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(shoppingCartRecipes, newShoppingCartIngreEntities)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        shoppingCartRecipes = newShoppingCartIngreEntities
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun addShoppingCart(shoppingCartEntity: ShoppingCartEntity) {
        mainViewModel.insertShoppingCart(shoppingCartEntity)
    }

    fun reduceShoppingCart(shoppingCartEntity: ShoppingCartEntity) {
        mainViewModel.reduceShoppingCart(shoppingCartEntity)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }
}