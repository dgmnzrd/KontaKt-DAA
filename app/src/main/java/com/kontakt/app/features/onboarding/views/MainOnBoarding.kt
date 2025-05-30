package com.kontakt.app.features.onboarding.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.data.local.datastore.StoreBoarding
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainOnBoarding(
    onFinish: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // TODO: reemplaza por tu Pager + páginas personalizadas
    Scaffold(
        bottomBar = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        StoreBoarding.setDone(ctx)
                        onFinish()
                    }
                }
            ) { Text("Empezar") }
        }
    ) { /* contenido */ }
}