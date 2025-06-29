package com.example.splitterapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.splitterapp.data.dao.GroupDao
import com.example.splitterapp.data.model.Group

@Database(entities = [Group::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "splitter_db"
                )
                    .fallbackToDestructiveMigration() // 🚨 Add this line to fix your crash
                    .build()
                    .also { instance = it }
            }
    }
}
