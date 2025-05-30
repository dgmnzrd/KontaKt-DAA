package com.kontakt.app.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos ORDER BY nombre")
    fun getAll(): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun getById(id: Long): Contacto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contacto: Contacto): Long

    @Update
    suspend fun update(contacto: Contacto)

    @Delete
    suspend fun delete(contacto: Contacto)
}