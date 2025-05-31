package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.kontakt.app.ui.theme.CambridgeBlue
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
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Name*") },
                isError = !nameOk && nombre.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apePat, onValueChange = { apePat = it },
                label = { Text("Last name*") },
                isError = !patOk && apePat.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apeMat, onValueChange = { apeMat = it },
                label = { Text("Middle name*") },
                isError = !matOk && apeMat.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value          = telefono,
                onValueChange  = { telefono = it },
                label          = { Text("Phone*") },
                isError        = !phoneOk && telefono.isNotEmpty(),
                supportingText = {
                    if (!phoneOk && telefono.isNotEmpty())
                        Text("10 dígitos o +XX XXX XXX XXXX")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value          = email,
                onValueChange  = { email = it },
                label          = { Text("Email") },
                isError        = !mailOk && email.isNotEmpty(),
                supportingText = {
                    if (!mailOk && email.isNotEmpty())
                        Text("Correo no válido")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Divider(Modifier.padding(top = 8.dp))

            /* ---------- dirección (opcional) ---------- */
            OutlinedTextField(calle , { calle  = it }, label = { Text("Street") },  modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(numExt, { numExt = it }, label = { Text("Ext.") }, modifier = Modifier.weight(1f))
                OutlinedTextField(numInt, { numInt = it }, label = { Text("Int.") }, modifier = Modifier.weight(1f))
                OutlinedTextField(numInt, { numInt = it }, label = { Text("Int.") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(colonia, { colonia = it }, label = { Text("District") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = cp, onValueChange = { cp = it },
                label = { Text("Postal code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(ciudad, { ciudad = it }, label = { Text("City") },   modifier = Modifier.fillMaxWidth())
            OutlinedTextField(estado, { estado = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth())

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