package com.kontakt.app.features.contactos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.data.local.database.Direccion
import com.kontakt.app.data.repository.ContactosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactoViewModel(private val repo: ContactosRepository) : ViewModel() {

    val contactos: Flow<List<Contacto>> = repo.contactos()

    fun save(
        nombre: String,
        apellidoP: String,
        apellidoM: String,
        telefono: String,
        email: String?,
        calle: String,
        numExt: String,
        numInt: String?,
        colonia: String,
        cp: String,
        ciudad: String,
        estado: String
    ) = viewModelScope.launch {
        val dir = Direccion(
            calle, numExt, numInt, colonia, cp, ciudad, estado
        )
        repo.agregar(
            Contacto(
                nombre = nombre,
                apellidoPaterno  = apellidoP,
                apellidoMaterno  = apellidoM,
                telefono = telefono,
                email    = email,
                direccion = dir
            )
        )
    }

    suspend fun get(id: Long) = repo.contacto(id)

    fun update(contacto: Contacto) = viewModelScope.launch { repo.actualizar(contacto) }

    fun delete(contacto: Contacto) = viewModelScope.launch { repo.eliminar(contacto) }
}