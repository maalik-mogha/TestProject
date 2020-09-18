package com.example.maalikflupertask.db

import androidx.room.*

@Dao
interface ProductDao {
    @Insert
    suspend fun addProduct(produt: Product)

    @Query("SELECT * FROM Product ORDER BY id DESC")
    suspend fun getAllProducts() : List<Product>

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)
}