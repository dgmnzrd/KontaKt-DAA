package com.kontakt.app.features.contactos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.data.repository.ContactosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactoViewModel(private val repo: ContactosRepository) : ViewModel() {

    val contactos: Flow<List<Contacto>> = repo.contactos()

    fun save(nombre: String, telefono: String, email: String?) = viewModelScope.launch {
        repo.agregar(Contacto(nombre = nombre, telefono = telefono, email = email))
    }

    fun update(contacto: Contacto) = viewModelScope.launch { repo.actualizar(contacto) }

    fun delete(contacto: Contacto) = viewModelScope.launch { repo.eliminar(contacto) }
}