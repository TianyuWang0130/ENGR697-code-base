package com.example.foody.data

import com.example.foody.data.database.RecipesDao
import com.example.foody.data.database.entities.*
import com.example.foody.models.FoodJoke
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    fun readFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipesDao.readFoodJoke()
    }

    fun readShoppingCart(): Flow<List<ShoppingCartEntity>> {
        return recipesDao.readShoppingCart()
    }

    fun readShoppingCartIngre(): Flow<List<ShoppingCartIngreEntity>> {
        return recipesDao.readShoppingCartIngredient()
    }

    fun readShoopingCartById(id: Int): List<ShoppingCartEntity> {
        return recipesDao.readShoppingCartById(id)
    }

    fun readShoppingCartIngreById(id: Int): List<ShoppingCartIngreEntity> {
        return recipesDao.readShoppingCartIngredientById(id)
    }

    fun readShoppingCartIngreByNameAndAmount(name: String, amount: Double): List<ShoppingCartIngreEntity> {
        return recipesDao.readShoppingCartIngredientByNameAndAmount(name, amount)
    }

    suspend fun insertShoppingCart(shoppingCartEntity: ShoppingCartEntity) {
        recipesDao.insertShoppingCart(shoppingCartEntity)
    }

    suspend fun insertShoppingCartIngre(shoppingCartIngreEntity: ShoppingCartIngreEntity) {
        recipesDao.insertShoppingCartIngre(shoppingCartIngreEntity)
    }

    suspend fun updateShoppingCart(num: Int, recipeId: Int) {
        recipesDao.updateShooppingCartById(num, recipeId)
    }

    suspend fun updateShoppingCartIngre(num: Int, id: Int) {
        recipesDao.updateShoppingCartIngreById(num, id)
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.insertFoodJoke(foodJokeEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteShoppingCartEntity(shoppingCartEntity: ShoppingCartEntity) {
        recipesDao.deleteShoppingCartEntity(shoppingCartEntity)
    }

    suspend fun deleteShoppingCartIngreEntity(shoppingCartIngreEntity: ShoppingCartIngreEntity) {
        recipesDao.deleteShoppingCartIngredientEntity(shoppingCartIngreEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

    suspend fun deleteAllShoppingCart() {
        recipesDao.deleteAllShoppingCart()
    }

    suspend fun deleteAllShoppingCartIngre() {
        recipesDao.deleteAllShoppingCartIngredient()
    }

}