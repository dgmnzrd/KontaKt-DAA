package com.kontakt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.core.navigation.withId
import com.kontakt.app.data.local.database.AppDatabase
import com.kontakt.app.data.local.datastore.StoreBoarding
import com.kontakt.app.data.repository.ContactosRepository
import com.kontakt.app.features.contactos.viewmodel.ContactoViewModel
import com.kontakt.app.features.contactos.views.AddView
import com.kontakt.app.features.contactos.views.DetailView
import com.kontakt.app.features.contactos.views.EditView
import com.kontakt.app.features.contactos.views.HomeView
import com.kontakt.app.features.onboarding.views.MainOnBoarding

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = ContactosRepository(AppDatabase.get(this).contactoDao())

        setContent {
            /* ----- determinar destino inicial ----- */
            val done by StoreBoarding
                .isDone(applicationContext)
                .collectAsState(initial = false)

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = if (done) NavRoutes.HOME else NavRoutes.ONBOARDING
            ) {

                /* ---------- ONBOARDING ---------- */
                composable(NavRoutes.ONBOARDING) {
                    MainOnBoarding {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                        }
                    }
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
                        vm = ContactoViewModel(repo),
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