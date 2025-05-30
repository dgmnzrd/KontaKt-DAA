package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel

@Composable
fun HomeView(
    vm: ContactoViewModel = viewModel(),
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val contactos by vm.contactos.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Text("+") }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contactos.size) { idx ->
                val c = contactos[idx]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(c.id) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(c.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(c.telefono)
                        c.email?.let { Text(it) }
                    }
                }
            }
        }
    }
}