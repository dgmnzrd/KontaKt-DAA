package com.kontakt.app.data.repository

import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.data.local.database.ContactoDao
import kotlinx.coroutines.flow.Flow

class ContactosRepository(private val dao: ContactoDao) {

    fun contactos(): Flow<List<Contacto>> = dao.getAll()

    suspend fun contacto(id: Long) = dao.getById(id)

    suspend fun agregar(contacto: Contacto) = dao.insert(contacto)

    suspend fun actualizar(contacto: Contacto) = dao.update(contacto)

    suspend fun eliminar(contacto: Contacto) = dao.delete(contacto)
}