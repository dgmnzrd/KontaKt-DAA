package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonFinish(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick  = onClick,
        colors   = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor   = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(50)
    ) {
        Text("Entrar", style = MaterialTheme.typography.titleMedium)
    }
}