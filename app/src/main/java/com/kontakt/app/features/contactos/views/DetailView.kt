package com.kontakt.app.features.contactos.views

/* ---------- Android & KotlinX ---------- */
import android.location.Geocoder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    contactoId: Long,
    vm: ContactoViewModel = viewModel(),
    onEdit: (Long) -> Unit,
    onBack: () -> Unit
) {
    /* ---------- load contact ---------- */
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    LaunchedEffect(contactoId) { contacto = vm.get(contactoId) }
    val scope = rememberCoroutineScope()

    /* ---------- geocoder ---------- */
    val ctx = LocalContext.current
    var latLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var geoTried by remember { mutableStateOf(false) }

    /* ---------- delete menu ---------- */
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
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                    /* ----- custom delete menu ----- */
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(
                            color  = MaterialTheme.colorScheme.errorContainer,
                            shape  = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showMenu = false
                                    contacto?.let {
                                        scope.launch {
                                            vm.delete(it)
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

            /* ---------- build address pretty + query ---------- */
            data class AddrInfo(val pretty: List<String>, val query: String?)
            val addr = remember(c.direccion) {
                with(c.direccion) {
                    val pretty = buildList {
                        val l1 = listOfNotNull(
                            calle.takeIf { it.isNotBlank() },
                            numeroExterior.takeIf { it.isNotBlank() },
                            numeroInterior?.takeIf { it.isNotBlank() }
                        ).joinToString(" ")
                        if (l1.isNotBlank()) add(l1)

                        val l2 = listOfNotNull(
                            colonia.takeIf { it.isNotBlank() },
                            codigoPostal.takeIf { it.isNotBlank() }?.let { "C.P. $it" }
                        ).joinToString(", ")
                        if (l2.isNotBlank()) add(l2)

                        val l3 = listOfNotNull(
                            ciudad.takeIf { it.isNotBlank() },
                            estado.takeIf { it.isNotBlank() }
                        ).joinToString(", ")
                        if (l3.isNotBlank()) add(l3)
                    }

                    val query =
                        when {
                            calle.isNotBlank() && ciudad.isNotBlank() ->
                                listOf(calle, numeroExterior, colonia, codigoPostal, ciudad, estado)
                                    .filter { it?.isNotBlank() == true }
                                    .joinToString(" ")
                            codigoPostal.isNotBlank() && (ciudad.isNotBlank() || estado.isNotBlank()) ->
                                listOf(codigoPostal, ciudad, estado).joinToString(" ")
                            else -> null
                        }
                    AddrInfo(pretty, query)
                }
            }

            /* ---------- one-shot geocoding ---------- */
            LaunchedEffect(addr.query) {
                if (!geoTried && addr.query != null) {
                    geoTried = true
                    latLng = withContext(Dispatchers.IO) {
                        try {
                            Geocoder(ctx)
                                .getFromLocationName(addr.query, 1)
                                ?.firstOrNull()
                                ?.let { Pair(it.latitude, it.longitude) }
                        } catch (_: IOException) { null }
                    }
                }
            }

            /* ---------- UI ---------- */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /* ----- avatar ----- */
                val avatarColors = listOf(
                    MaterialTheme.colorScheme.secondary,   // Copper
                    MaterialTheme.colorScheme.tertiary     // Payne Gray
                )
                val avatarColor = remember(c.nombre) {
                    val idx = (c.nombre.hashCode() and 0x7fffffff) % avatarColors.size
                    avatarColors[idx]
                }

                Box(
                    Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
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
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                /* ----- phone & mail card ----- */
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Phone",  style = MaterialTheme.typography.labelLarge)
                        Text(c.telefono, style = MaterialTheme.typography.bodyLarge)

                        c.email?.let {
                            Spacer(Modifier.height(8.dp))
                            Text("Email", style = MaterialTheme.typography.labelLarge)
                            Text(it,      style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                /* ----- address card (if any) ----- */
                if (addr.pretty.isNotEmpty()) {
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
                            addr.pretty.forEach {
                                Text(it, style = MaterialTheme.typography.bodyLarge)
                            }
                            latLng?.let { (lat, lon) ->
                                Spacer(Modifier.height(6.dp))

                                // to open Google-Maps when tapped we need a context + intent
                                val context = LocalContext.current
                                val mapsUri = remember(lat, lon) {
                                    android.net.Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                                }

                                Text(
                                    "Lat: %.6f, Lon: %.6f".format(lat, lon),
                                    style     = MaterialTheme.typography.labelLarge,
                                    color     = MaterialTheme.colorScheme.onPrimary,
                                    modifier  = Modifier
                                        .clickable {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                mapsUri
                                            ).apply {
                                                // force Google Maps if installed, otherwise let the system choose
                                                setPackage("com.google.android.apps.maps")
                                            }
                                            context.startActivity(intent)
                                        }
                                        .padding(top = 2.dp)          // small touch-target help
                                )
                            }
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

/* util – first letter */
private fun inicial(text: String): String =
    Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .uppercase()
        .firstOrNull()?.toString() ?: "?"