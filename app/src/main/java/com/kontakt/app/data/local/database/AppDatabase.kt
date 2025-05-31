package com.kontakt.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Contacto::class],
    version = 2,               // <-- sube la versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactoDao(): ContactoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kontakt_db"
                )
                    /* ⚠️  Solo para desarrollo — recrea la DB al cambiar versión */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}