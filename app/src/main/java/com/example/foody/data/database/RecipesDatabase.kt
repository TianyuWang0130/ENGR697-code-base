package com.example.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foody.data.database.entities.*

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FoodJokeEntity::class, ShoppingCartEntity::class, ShoppingCartIngreEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

}