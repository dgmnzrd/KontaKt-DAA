package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.data.local.database.Direccion
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.ui.theme.White
import kotlinx.coroutines.launch
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    contactoId: Long,
    vm: ContactoViewModel = viewModel(),
    onBack: () -> Unit
) {
    /* ---------- carga ---------- */
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    LaunchedEffect(contactoId) { contacto = vm.get(contactoId) }

    val scope = rememberCoroutineScope()

    if (contacto == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    /* ---------- estados ---------- */
    var nombre   by remember { mutableStateOf(contacto!!.nombre) }
    var apePat   by remember { mutableStateOf(contacto!!.apellidoPaterno) }
    var apeMat   by remember { mutableStateOf(contacto!!.apellidoMaterno) }
    var telefono by remember { mutableStateOf(contacto!!.telefono) }
    var email    by remember { mutableStateOf(contacto!!.email ?: "") }

    val dir      = contacto!!.direccion
    var calle    by remember { mutableStateOf(dir.calle) }
    var numExt   by remember { mutableStateOf(dir.numeroExterior) }
    var numInt   by remember { mutableStateOf(dir.numeroInterior ?: "") }
    var colonia  by remember { mutableStateOf(dir.colonia) }
    var cp       by remember { mutableStateOf(dir.codigoPostal) }
    var ciudad   by remember { mutableStateOf(dir.ciudad) }
    var estado   by remember { mutableStateOf(dir.estado) }

    /* ---------- regex ---------- */
    val PHONE_RE = Regex("""^(\d{10})$|^\+\d{1,3}\s\d{3}(?:\s\d{3}\s\d{4})$""")
    val MAIL_RE  = Regex("""^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$""")

    /* ---------- validaciones ---------- */
    val nameOk  = nombre.isNotBlank()
    val patOk   = apePat.isNotBlank()
    val matOk   = apeMat.isNotBlank()
    val phoneOk = telefono.matches(PHONE_RE)
    val mailOk  = email.isBlank() || email.matches(MAIL_RE)

    val formOk  = nameOk && patOk && matOk && phoneOk && mailOk

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit contact",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onBack) {
                        Text("Cancel",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Button(
                        enabled = formOk,
                        shape   = RoundedCornerShape(50),
                        onClick = {
                            scope.launch {
                                vm.update(
                                    contacto!!.copy(
                                        nombre          = nombre,
                                        apellidoPaterno = apePat,
                                        apellidoMaterno = apeMat,
                                        telefono        = telefono,
                                        email           = email.ifBlank { null },
                                        direccion = Direccion(
                                            calle          = calle,
                                            numeroExterior = numExt,
                                            numeroInterior = numInt.ifBlank { null },
                                            colonia        = colonia,
                                            codigoPostal   = cp,
                                            ciudad         = ciudad,
                                            estado         = estado
                                        )
                                    )
                                )
                                onBack()
                            }
                        }
                    ) {
                        Text(
                            "Save",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            /* ---------- avatar (color consistente con Home) ---------- */
            val avatarColors = listOf(
                MaterialTheme.colorScheme.secondary,   // Copper
                MaterialTheme.colorScheme.tertiary     // Payne Gray
            )
            /* índice determinista a partir del nombre -- igual que en HomeView */
            val avatarColor = remember(nombre) {
                val idx = (nombre.hashCode() and 0x7fffffff) % avatarColors.size
                avatarColors[idx]
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(avatarColor),          // ← color calculado
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initial(nombre),
                    color  = White,
                    style  = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            /* ---------- formulario ---------- */
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Name*") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                isError = !nameOk && nombre.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apePat,
                onValueChange = { apePat = it },
                label = { Text("Last name*") },
                leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                isError = !patOk && apePat.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apeMat,
                onValueChange = { apeMat = it },
                label = { Text("Middle name*") },
                leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                isError = !matOk && apeMat.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Phone*") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                isError = !phoneOk && telefono.isNotEmpty(),
                supportingText = {
                    if (!phoneOk && telefono.isNotEmpty())
                        Text("10 dígitos o +XX XXX XXX XXXX")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null) },
                isError = !mailOk && email.isNotEmpty(),
                supportingText = {
                    if (!mailOk && email.isNotEmpty())
                        Text("Correo no válido")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Divider(Modifier.padding(top = 8.dp))

            /* ---------- dirección (opcional) ---------- */
            OutlinedTextField(
                value = calle,
                onValueChange = { calle = it },
                label = { Text("Street") },
                leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = numExt,
                    onValueChange = { numExt = it },
                    label = { Text("Ext.") },
                    leadingIcon = { Icon(Icons.Filled.House, contentDescription = null) },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
                OutlinedTextField(
                    value = numInt,
                    onValueChange = { numInt = it },
                    label = { Text("Int.") },
                    leadingIcon = { Icon(Icons.Filled.MeetingRoom, contentDescription = null) },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
            }

            OutlinedTextField(
                value = colonia,
                onValueChange = { colonia = it },
                label = { Text("District") },
                leadingIcon = { Icon(Icons.Filled.Map, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cp,
                onValueChange = { cp = it },
                label = { Text("Postal code") },
                leadingIcon = { Icon(Icons.Filled.MarkunreadMailbox, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("City") },
                leadingIcon = { Icon(Icons.Filled.LocationCity, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado,
                onValueChange = { estado = it },
                label = { Text("State") },
                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}

/* ---------- util ---------- */
private fun initial(text: String): String =
    Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .uppercase()
        .firstOrNull()?.toString() ?: "#"