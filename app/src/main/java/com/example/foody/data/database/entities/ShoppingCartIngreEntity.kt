package com.example.foody.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.ExtendedIngredient
import com.example.foody.util.Constants

@Entity(tableName = Constants.SHOPPING_CART_INGREDIENT_TABLE)
class ShoppingCartIngreEntity(
    @Embedded
    var foodIngre: ExtendedIngredient,
    var number: Int


) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}