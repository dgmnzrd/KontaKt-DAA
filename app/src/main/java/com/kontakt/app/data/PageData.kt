package com.kontakt.app.data

import androidx.annotation.RawRes

data class PageData(
    @RawRes val animRes: Int,
    val titulo: String,
    val descripcion: String
)