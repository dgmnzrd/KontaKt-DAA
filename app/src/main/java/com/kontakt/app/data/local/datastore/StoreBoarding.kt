package com.kontakt.app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "kontakt_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object StoreBoarding {

    private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")

    /** Flujo que emite true si el onboarding ya se mostró */
    fun isDone(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[ONBOARDING_DONE] ?: false }

    /** Marca el onboarding como completado */
    suspend fun setDone(context: Context) {
        context.dataStore.edit { it[ONBOARDING_DONE] = true }
    }

    /** Para pruebas o reinstalaciones */
    suspend fun clear(context: Context) {
        context.dataStore.edit { it.remove(ONBOARDING_DONE) }
    }
}