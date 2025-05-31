package com.kontakt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.core.navigation.withId
import com.kontakt.app.data.local.database.AppDatabase
import com.kontakt.app.data.local.datastore.StoreBoarding
import com.kontakt.app.data.repository.ContactosRepository
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.features.contactos.views.*
import com.kontakt.app.features.onboarding.views.MainOnBoarding
import com.kontakt.app.ui.theme.KontaKtTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = ContactosRepository(AppDatabase.get(this).contactoDao())

        setContent {
            KontaKtTheme {

                /* 1️⃣  Leer DataStore — valor nulo mientras carga */
                val done: Boolean? by StoreBoarding
                    .isDone(applicationContext)
                    .collectAsState(initial = null)

                if (done == null) {
                    Box(Modifier.fillMaxSize())       // ← splash en blanco (puedes cambiarlo)
                    return@KontaKtTheme
                }

                /* 2️⃣  Navegación principal */
                val navController = rememberNavController()

                NavHost(
                    navController  = navController,
                    startDestination = if (done == true) NavRoutes.HOME else NavRoutes.ONBOARDING
                ) {

                    /* ---------- ONBOARDING ---------- */
                    composable(NavRoutes.ONBOARDING) {
                        MainOnBoarding(nav = navController, store = StoreBoarding)
                    }

                    /* ---------- HOME ---------- */
                    composable(NavRoutes.HOME) {
                        HomeView(
                            vm     = ContactoViewModel(repo),
                            onAdd  = { navController.navigate(NavRoutes.ADD) },
                            onView = { id -> navController.navigate(NavRoutes.DETAIL.withId(id)) },
                            onEdit = { id -> navController.navigate(NavRoutes.EDIT.withId(id)) }
                        )
                    }

                    /* ---------- ADD ---------- */
                    composable(NavRoutes.ADD) {
                        AddView(
                            vm     = ContactoViewModel(repo),
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------- DETAIL ---------- */
                    composable(NavRoutes.DETAIL) { backEntry ->
                        val id = backEntry.arguments?.getString("contactId")?.toLong() ?: return@composable
                        DetailView(
                            contactoId = id,
                            vm    = ContactoViewModel(repo),
                            onBack = { navController.popBackStack() }
                        )
                    }

                    /* ---------- EDIT ---------- */
                    composable(NavRoutes.EDIT) { backEntry ->
                        val id = backEntry.arguments?.getString("contactId")?.toLong() ?: return@composable
                        EditView(
                            contactoId = id,
                            vm    = ContactoViewModel(repo),
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}