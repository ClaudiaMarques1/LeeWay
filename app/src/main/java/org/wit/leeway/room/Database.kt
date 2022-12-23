package org.wit.leeway.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.wit.leeway.helpers.Converters
import org.wit.leeway.models.HabitModel

@Database(entities = [HabitModel::class], version = 2,  exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}