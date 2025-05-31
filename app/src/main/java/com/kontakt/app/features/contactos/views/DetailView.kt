package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    contactoId: Long,
    vm: ContactoViewModel = viewModel(),
    onBack: () -> Unit
) {
    /* ---------- Cargar contacto ---------- */
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    LaunchedEffect(contactoId) {
        contacto = vm.get(contactoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        contacto?.let { c ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                /* ---- Datos guardados ---- */
                Text("Nombre completo", style = MaterialTheme.typography.labelLarge)
                Text("${c.nombre} ${c.apellidoPaterno} ${c.apellidoMaterno}")

                Text("Teléfono", style = MaterialTheme.typography.labelLarge)
                Text(c.telefono)

                c.email?.let {
                    Text("Mail", style = MaterialTheme.typography.labelLarge)
                    Text(it)
                }

                Text("Dirección", style = MaterialTheme.typography.labelLarge)
                with(c.direccion) {
                    Text("$calle $numeroExterior ${numeroInterior ?: ""}")
                    Text("$colonia, C.P. $codigoPostal")
                    Text("$ciudad, $estado")
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}