package com.kontakt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.core.navigation.withId
import com.kontakt.app.data.local.database.AppDatabase
import com.kontakt.app.data.local.datastore.StoreBoarding
import com.kontakt.app.data.repository.ContactosRepository
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.features.contactos.views.HomeView
import com.kontakt.app.features.onboarding.views.MainOnBoarding
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = ContactosRepository(AppDatabase.get(this).contactoDao())

        setContent {
            var startDest by remember { mutableStateOf(NavRoutes.ONBOARDING) }

            // Leer DataStore al arranque
            LaunchedEffect(Unit) {
                StoreBoarding.isDone(this@MainActivity).collect { done ->
                    startDest = if (done) NavRoutes.HOME else NavRoutes.ONBOARDING
                }
            }

            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = startDest) {

                composable(NavRoutes.ONBOARDING) {
                    MainOnBoarding { nav.navigate(NavRoutes.HOME) { popUpTo(0) } }
                }
                composable(NavRoutes.HOME) {
                    HomeView(
                        vm = ContactoViewModel(repo),
                        onAdd = { nav.navigate(NavRoutes.ADD) },
                        onEdit = { id -> nav.navigate(NavRoutes.EDIT.withId(id)) }
                    )
                }
                // composable(NavRoutes.ADD) { AddView(...) }
                // composable(NavRoutes.EDIT) { backStackEntry -> EditView(...) }
            }
        }
    }
}