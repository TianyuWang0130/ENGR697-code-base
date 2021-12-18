package com.example.foody.bindingadapters

import android.view.View
import android.widget.Button
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.adapters.ShoppingCartAdapter
import com.example.foody.data.database.entities.ShoppingCartEntity
import com.example.foody.models.Result

class ShoppingCartBinding {

    companion object {
        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(view: View, shoppingCartEntity: List<ShoppingCartEntity>?, mAdapter: ShoppingCartAdapter?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = shoppingCartEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if(!dataCheck){
                        shoppingCartEntity?.let { mAdapter?.setData(it) }
                    }
                }
                else -> view.isVisible = shoppingCartEntity.isNullOrEmpty()
            }
        }


    }
}