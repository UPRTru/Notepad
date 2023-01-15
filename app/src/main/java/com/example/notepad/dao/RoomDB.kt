package com.example.notepad.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Page::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDB: RoomDatabase() {
    abstract fun pageDAO(): DAO

    companion object {
        private var INSTANCE: RoomDB? = null

        fun getInstance(context: Context): RoomDB? {
            if (INSTANCE == null) {
                synchronized(RoomDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        RoomDB::class.java, "Pages").allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}