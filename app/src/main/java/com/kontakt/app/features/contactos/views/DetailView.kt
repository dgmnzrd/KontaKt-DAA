package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.ui.theme.White
import kotlinx.coroutines.launch
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    contactoId: Long,
    vm: ContactoViewModel = viewModel(),
    onEdit: (Long) -> Unit,
    onBack: () -> Unit
) {
    /* ---------- carga ---------- */
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    LaunchedEffect(contactoId) { contacto = vm.get(contactoId) }
    val scope = rememberCoroutineScope()

    /* ---------- menú eliminar ---------- */
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },

                        /* ←  fondo completo del menú */
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        /* Ítem único – ocupa todo el ancho, sin paddings extra */
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showMenu = false
                                    contacto?.let { c ->
                                        scope.launch {
                                            vm.delete(c)
                                            onBack()
                                        }
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            )
        },

        floatingActionButton = {
            contacto?.let {
                FloatingActionButton(
                    onClick = { onEdit(it.id) },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            }
        },

        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        contacto?.let { c ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /* ---------- avatar + nombre ---------- */
                val avatarColors = listOf(
                    MaterialTheme.colorScheme.secondary,   // Copper
                    MaterialTheme.colorScheme.tertiary     // Payne Gray
                )
                val avatarColor = remember(c.nombre) {
                    val idx = (c.nombre.hashCode() and 0x7fffffff) % avatarColors.size
                    avatarColors[idx]
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(avatarColor),          // ← mismo color que en Home
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        inicial(c.nombre),
                        color = White,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    "${c.nombre} ${c.apellidoPaterno} ${c.apellidoMaterno}",
                    style  = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color  = MaterialTheme.colorScheme.onBackground         // negro
                )

                /* ---------- teléfono & mail ---------- */
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Phone", style = MaterialTheme.typography.labelLarge)
                        Text(c.telefono, style = MaterialTheme.typography.bodyLarge)

                        c.email?.let {
                            Spacer(Modifier.height(8.dp))
                            Text("Email", style = MaterialTheme.typography.labelLarge)
                            Text(it, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                /* ---------- dirección (solo si hay datos) ---------- */
                val addressLines = remember(c.direccion) {
                    buildList {
                        with(c.direccion) {

                            // Línea 1 – calle + números
                            // Línea 1 – calle + números
                            val l1 = listOfNotNull(
                                calle.takeIf         { it.isNotBlank() },
                                numeroExterior.takeIf{ it.isNotBlank() },
                                /* ← aquí va el safe-call */
                                numeroInterior?.takeIf { it.isNotBlank() }
                            ).joinToString(" ")
                            if (l1.isNotBlank()) add(l1)

                            // Línea 2 – colonia y C.P.
                            val l2 = listOfNotNull(
                                colonia.takeIf { it.isNotBlank() },
                                codigoPostal.takeIf { it.isNotBlank() }?.let { "C.P. $it" }
                            ).joinToString(", ")
                            if (l2.isNotBlank()) add(l2)

                            // Línea 3 – ciudad y estado
                            val l3 = listOfNotNull(
                                ciudad.takeIf { it.isNotBlank() },
                                estado.takeIf { it.isNotBlank() }
                            ).joinToString(", ")
                            if (l3.isNotBlank()) add(l3)
                        }
                    }
                }

                if (addressLines.isNotEmpty()) {                    // ← solo se muestra si hay algo
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor   = White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Address", style = MaterialTheme.typography.labelLarge)
                            addressLines.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
                        }
                    }
                }
            }
        } ?: Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
    }
}

/* util – primera letra */
private fun inicial(text: String): String =
    Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .uppercase()
        .firstOrNull()?.toString() ?: "?"