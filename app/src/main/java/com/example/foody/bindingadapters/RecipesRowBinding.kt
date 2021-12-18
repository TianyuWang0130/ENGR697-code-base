package com.example.foody.bindingadapters

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import coil.load
import com.example.foody.R
import com.example.foody.adapters.ShoppingCartAdapter
import com.example.foody.data.database.entities.ShoppingCartEntity
import com.example.foody.models.Result
import com.example.foody.ui.fragments.recipes.RecipesFragmentDirections
import com.example.foody.util.Constants
import com.example.foody.viewmodels.MainViewModel
import org.jsoup.Jsoup
import java.lang.Exception

class RecipesRowBinding {

    companion object {

        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result) {
            Log.d("onRecipeClickListener", "CALLED")
            recipeRowLayout.setOnClickListener {
                try {
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onRecipeClickListener", e.toString())
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        @BindingAdapter("loadIngreImageFromUrl")
        @JvmStatic
        fun loadIngreImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(Constants.BASE_IMAGE_URL + imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?){
            if(description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }

        @BindingAdapter("loadInt")
        @JvmStatic
        fun loadInt(textView: TextView, num: Int?){
            textView.text = num.toString()
        }

        @BindingAdapter("loadDouble")
        @JvmStatic
        fun loadDouble(textView: TextView, num: Double?){
            textView.text = num.toString()
        }

        @BindingAdapter("onAddBtnClickListener", "setData", requireAll = true)
        @JvmStatic
        fun onAddBtnClickListener(addBtn: Button, shoppingCartEntity: ShoppingCartEntity?, mAdapter: ShoppingCartAdapter?) {
            addBtn.setOnClickListener{
                if (shoppingCartEntity != null) {
                    mAdapter?.addShoppingCart(shoppingCartEntity)
                }
            }
        }

        @BindingAdapter("onMinusBtnClickListener", "setData", requireAll = true)
        @JvmStatic
        fun onMinusBtnClickListener(reduceBtn: Button, shoppingCartEntity: ShoppingCartEntity?, mAdapter: ShoppingCartAdapter?) {
            reduceBtn.setOnClickListener{
                if (shoppingCartEntity != null) {
                    mAdapter?.reduceShoppingCart(shoppingCartEntity)
                }
            }
        }

    }

}