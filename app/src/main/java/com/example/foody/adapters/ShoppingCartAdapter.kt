package com.example.foody.adapters

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.data.database.entities.ShoppingCartEntity
import com.example.foody.databinding.ShoppingCartRowLayoutBinding
import com.example.foody.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import com.example.foody.util.RecipesDiffUtil
import com.example.foody.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar

class ShoppingCartAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<ShoppingCartAdapter.MyViewHolder>(), ActionMode.Callback{

    private var multiSelection = false

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var selectedRecipes = arrayListOf<ShoppingCartEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    private var shoppingCartRecipes = emptyList<ShoppingCartEntity>()

    class MyViewHolder(val binding: ShoppingCartRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shoppingCartEntity: ShoppingCartEntity) {
            binding.shoppingCartEntity = shoppingCartEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ShoppingCartRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = shoppingCartRecipes[position]
        holder.bind(currentRecipe)

        saveItemStateOnScroll(currentRecipe, holder)

        /**
         * Single Click Listener
         * */
        holder.binding.shoppingCartRowLayout.setOnClickListener {
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
        holder.binding.shoppingCartRowLayout.setOnLongClickListener {
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
            mainViewModel.insertShoppingCart(currentRecipe)
        }

        holder.binding.shoppingCartItemReduce.setOnClickListener {
            mainViewModel.reduceShoppingCart(currentRecipe)
        }

    }

    private fun saveItemStateOnScroll(currentRecipe: ShoppingCartEntity, holder: MyViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: ShoppingCartEntity) {
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
        holder.binding.shoppingCartRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.shoppingCartRowCardView.strokeColor =
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
                mainViewModel.deleteShoppingCartEntity(it)
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

    fun setData(newShoppingCartEntities: List<ShoppingCartEntity>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(shoppingCartRecipes, newShoppingCartEntities)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        shoppingCartRecipes = newShoppingCartEntities
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