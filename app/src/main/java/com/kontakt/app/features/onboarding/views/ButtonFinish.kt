package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonFinish(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(0.8f),
        shape     = RoundedCornerShape(30.dp)
    ) {
        Text("Entrar")
    }
}