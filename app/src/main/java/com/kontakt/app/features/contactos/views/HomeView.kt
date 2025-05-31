package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    vm: ContactoViewModel = viewModel(),
    onAdd   : () -> Unit,
    onView  : (Long) -> Unit,
    onEdit  : (Long) -> Unit
) {
    val contactos by vm.contactos.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contactos, key = { it.id }) { c: Contacto ->

                /* ---------- Acción EDIT (swipe derecha) ---------- */
                val editAction = SwipeAction(
                    icon        = { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White) },
                    background  = Color(0xFF4CAF50),
                    onSwipe     = { onEdit(c.id) }
                )

                /* ---------- Acción DELETE (swipe izquierda) ---------- */
                val deleteAction = SwipeAction(
                    icon        = { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White) },
                    background  = Color(0xFFE53935),
                    isUndo      = true,                // permite deshacer si lo deseas
                    onSwipe     = { vm.delete(c) }      // elimina de Room
                )

                SwipeableActionsBox(
                    startActions = listOf(editAction),   // deslizar de izquierda a derecha
                    endActions   = listOf(deleteAction), // deslizar de derecha a izquierda
                    swipeThreshold = 100.dp
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .clickable { onView(c.id) }
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                "${c.nombre} ${c.apellidoPaterno} ${c.apellidoMaterno}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(c.telefono)
                            c.email?.let { Text(it) }
                            Text("${c.direccion.ciudad}, ${c.direccion.estado}")
                        }
                    }
                }
            }
        }
    }
}