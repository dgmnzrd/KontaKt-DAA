package com.kontakt.app.data.local.database

import androidx.room.ColumnInfo

data class Direccion(
    val calle: String       = "",
    @ColumnInfo(name = "num_ext")  val numeroExterior: String = "",
    @ColumnInfo(name = "num_int")  val numeroInterior: String? = null,
    val colonia: String     = "",
    @ColumnInfo(name = "cp")       val codigoPostal: String = "",
    val ciudad: String      = "",
    val estado: String      = ""
)