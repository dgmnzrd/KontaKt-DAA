package com.kontakt.app.data.local.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactos")
data class Contacto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /* --- Datos personales --- */
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,

    /* --- Contacto --- */
    val telefono: String,
    val email: String? = null,

    /* --- Dirección --- */
    @Embedded val direccion: Direccion
)