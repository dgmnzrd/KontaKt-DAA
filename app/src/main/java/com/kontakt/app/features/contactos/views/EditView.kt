package com.kontakt.app.features.contactos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.data.local.database.Contacto
import com.kontakt.app.data.local.database.Direccion
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    contactoId: Long,
    vm: ContactoViewModel = viewModel(),
    onBack: () -> Unit
) {
    /* ---------- carga del contacto ---------- */
    var contacto by remember { mutableStateOf<Contacto?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(contactoId) { contacto = vm.get(contactoId) }

    if (contacto == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    /* ---------- estados mutables pre-cargados ---------- */
    var nombre         by remember { mutableStateOf(contacto!!.nombre) }
    var apellidoP      by remember { mutableStateOf(contacto!!.apellidoPaterno) }
    var apellidoM      by remember { mutableStateOf(contacto!!.apellidoMaterno) }
    var telefono       by remember { mutableStateOf(contacto!!.telefono) }
    var email          by remember { mutableStateOf(contacto!!.email ?: "") }

    val dir = contacto!!.direccion
    var calle          by remember { mutableStateOf(dir.calle) }
    var numExt         by remember { mutableStateOf(dir.numeroExterior) }
    var numInt         by remember { mutableStateOf(dir.numeroInterior ?: "") }
    var colonia        by remember { mutableStateOf(dir.colonia) }
    var cp             by remember { mutableStateOf(dir.codigoPostal) }
    var ciudad         by remember { mutableStateOf(dir.ciudad) }
    var estado         by remember { mutableStateOf(dir.estado) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar contacto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        vm.update(
                            contacto!!.copy(
                                nombre = nombre,
                                apellidoPaterno = apellidoP,
                                apellidoMaterno = apellidoM,
                                telefono = telefono,
                                email = email.ifBlank { null },
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
            ) { Icon(Icons.Filled.Check, contentDescription = "Guardar") }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(nombre, { nombre = it },  label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(apellidoP, { apellidoP = it }, label = { Text("Apellido paterno") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(apellidoM, { apellidoM = it }, label = { Text("Apellido materno") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(telefono,  { telefono  = it }, label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email,     { email     = it }, label = { Text("Mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth())

            Divider()

            OutlinedTextField(calle, { calle = it }, label = { Text("Calle") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(numExt, { numExt = it }, label = { Text("Num. ext") }, modifier = Modifier.weight(1f))
                OutlinedTextField(numInt, { numInt = it }, label = { Text("Num. int (opcional)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(colonia, { colonia = it }, label = { Text("Colonia") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(cp, { cp = it }, label = { Text("C.P.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ciudad, { ciudad = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(estado, { estado = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(80.dp))
        }
    }
}