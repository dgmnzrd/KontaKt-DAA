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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddView(
    vm: ContactoViewModel = viewModel(),
    onBack: () -> Unit
) {
    /* --- Estados de los campos --- */
    var nombre         by remember { mutableStateOf("") }
    var apellidoP      by remember { mutableStateOf("") }
    var apellidoM      by remember { mutableStateOf("") }
    var telefono       by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var calle          by remember { mutableStateOf("") }
    var numExt         by remember { mutableStateOf("") }
    var numInt         by remember { mutableStateOf("") }
    var colonia        by remember { mutableStateOf("") }
    var cp             by remember { mutableStateOf("") }
    var ciudad         by remember { mutableStateOf("") }
    var estado         by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo contacto") },
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
                    vm.save(
                        nombre, apellidoP, apellidoM, telefono, email.ifBlank { null },
                        calle, numExt, numInt.ifBlank { null }, colonia, cp, ciudad, estado
                    )
                    onBack()
                }
            ) { Icon(Icons.Filled.Check, contentDescription = "Guardar") }
        }
    ) { padding ->

        /* ---------- CONTENIDO CON SCROLL ---------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* --- Datos personales --- */
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellidoP, onValueChange = { apellidoP = it },
                label = { Text("Apellido paterno") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellidoM, onValueChange = { apellidoM = it },
                label = { Text("Apellido materno") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = telefono, onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            /* --- Dirección --- */
            OutlinedTextField(
                value = calle, onValueChange = { calle = it },
                label = { Text("Calle") }, modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = numExt, onValueChange = { numExt = it },
                    label = { Text("Num. ext") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = numInt, onValueChange = { numInt = it },
                    label = { Text("Num. int (opcional)") }, modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = colonia, onValueChange = { colonia = it },
                label = { Text("Colonia") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cp, onValueChange = { cp = it },
                label = { Text("C.P.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ciudad, onValueChange = { ciudad = it },
                label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado, onValueChange = { estado = it },
                label = { Text("Estado") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(80.dp)) // espacio para flotar sobre FAB
        }
    }
}