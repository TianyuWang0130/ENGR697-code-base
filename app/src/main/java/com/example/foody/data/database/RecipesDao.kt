package com.example.foody.data.database

import androidx.room.*
import com.example.foody.data.database.entities.*
import com.example.foody.models.FoodJoke
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingCart(shoppingCartEntity: ShoppingCartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingCartIngre(shoppingCartIngreEntity: ShoppingCartIngreEntity)

    @Query("UPDATE shopping_cart_table SET number = :num WHERE recipeId = :id")
    fun updateShooppingCartById(num: Int, id: Int)

    @Query("UPDATE shopping_cart_ingredient_table SET number = :num WHERE id = :id")
    fun updateShoppingCartIngreById(num: Int, id: Int)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    @Query("SELECT * FROM shopping_cart_table ORDER BY id ASC")
    fun readShoppingCart(): Flow<List<ShoppingCartEntity>>

    @Query("SELECT * FROM shopping_cart_table WHERE recipeId = :id")
    fun readShoppingCartById(id: Int): List<ShoppingCartEntity>

    @Query("SELECT * FROM shopping_cart_ingredient_table ORDER BY id ASC")
    fun readShoppingCartIngredient(): Flow<List<ShoppingCartIngreEntity>>

    @Query("SELECT * FROM shopping_cart_ingredient_table WHERE id = :id")
    fun readShoppingCartIngredientById(id: Int): List<ShoppingCartIngreEntity>

    @Query("SELECT * FROM shopping_cart_ingredient_table WHERE name = :name AND amount = :amount")
    fun readShoppingCartIngredientByNameAndAmount(name: String, amount: Double): List<ShoppingCartIngreEntity>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteShoppingCartEntity(shoppingCartEntity: ShoppingCartEntity)

    @Delete
    suspend fun deleteShoppingCartIngredientEntity(shoppingCartIngreEntity: ShoppingCartIngreEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

    @Query("DELETE FROM shopping_cart_table")
    suspend fun deleteAllShoppingCart()

    @Query("DELETE FROM shopping_cart_ingredient_table")
    suspend fun deleteAllShoppingCartIngredient()

}