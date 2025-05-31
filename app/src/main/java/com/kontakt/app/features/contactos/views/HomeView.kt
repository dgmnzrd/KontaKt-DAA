package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.ui.theme.White
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    vm: ContactoViewModel = viewModel(),
    onAdd  : () -> Unit,
    onView : (Long) -> Unit,
    onEdit : (Long) -> Unit
) {
    /* ------ datos y filtro ------ */
    val contactos by vm.contactos.collectAsState(initial = emptyList())
    var searchMode  by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val listaFiltrada = remember(contactos, searchQuery) {
        if (searchQuery.isBlank()) contactos
        else contactos.filter {
            "${it.nombre} ${it.apellidoPaterno} ${it.apellidoMaterno}"
                .contains(searchQuery, ignoreCase = true)
        }
    }

    /* agrupamos por letra SOLO cuando no hay búsqueda */
    val agrupados = remember(listaFiltrada, searchQuery) {
        if (searchQuery.isBlank())
            listaFiltrada.sortedBy { it.nombre.lowercase() }
                .groupBy { letraInicial(it.nombre) }
        else mapOf("_" to listaFiltrada)
    }

    Scaffold(
        topBar = {
            if (searchMode) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder   = { Text("Buscar…") },
                            singleLine    = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor   = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor   = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { searchMode = false; searchQuery = "" }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    actions = {}
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Contacts",
                            style = MaterialTheme.typography.displaySmall      // ← MISMO estilo
                                .copy(fontWeight = FontWeight.Bold),
                        )
                    },
                    actions = {
                        IconButton(onClick = { searchMode = true }) {
                            Icon(Icons.Default.Search, "Buscar")
                        }
                        IconButton(onClick = onAdd) {
                            Icon(Icons.Default.Add, "Añadir")
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        /* ---------- CONTENEDOR list ---------- */
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            color  = MaterialTheme.colorScheme.primaryContainer,   // CambridgeBlueLight
            shape  = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp
        ) {
            LazyColumn(
                contentPadding       = PaddingValues(16.dp),
                verticalArrangement  = Arrangement.spacedBy(10.dp)
            ) {
                agrupados.forEach { (letra, lista) ->

                    if (letra != "_") {
                        item("header_$letra") {
                            Text(
                                letra,                                       // A, B, C…
                                style = MaterialTheme.typography.displaySmall   // mismo alto que título
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = MaterialTheme.typography.titleMedium.fontSize
                                    ),
                                color  = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    items(lista, key = { it.id }) { contacto ->

                        val edit = SwipeAction(
                            icon       = { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onSecondary) },
                            background = MaterialTheme.colorScheme.secondary,
                            onSwipe    = { onEdit(contacto.id) }
                        )
                        val delete = SwipeAction(
                            icon       = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onError) },
                            background = MaterialTheme.colorScheme.error,
                            isUndo     = true,
                            onSwipe    = { vm.delete(contacto) }
                        )

                        SwipeableActionsBox(
                            startActions = listOf(edit),
                            endActions   = listOf(delete),
                            swipeThreshold = 96.dp
                        ) {
                            ContactCard(contacto) { onView(contacto.id) }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Tarjeta ---------- */
@Composable
private fun ContactCard(contacto: Contacto, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(contacto.nombre)
            Spacer(Modifier.width(16.dp))
            Text(
                "${contacto.nombre} ${contacto.apellidoPaterno} ${contacto.apellidoMaterno}",
                style = MaterialTheme.typography.displaySmall         // misma tipografía
                    .copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = MaterialTheme.typography.bodyLarge.fontSize,  // ‼️ reduce tamaño para que no sea enorme
                        color      = MaterialTheme.colorScheme.onSurface           // negro en tema claro
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ---------- Avatar circular ---------- */
@Composable
private fun Avatar(name: String) {

    // Dos colores permitidos
    val avatarColors = listOf(
        MaterialTheme.colorScheme.secondary,   // Copper
        MaterialTheme.colorScheme.tertiary     // Payne Gray
    )

    // Índice "aleatorio" pero determinista por nombre
    val color = remember(name) {
        val idx = (name.hashCode() and 0x7fffffff) % avatarColors.size
        avatarColors[idx]
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            letraInicial(name),
            color = White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/* ---------- util ---------- */
private fun letraInicial(text: String): String =
    Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .uppercase()
        .firstOrNull()?.toString() ?: "#"