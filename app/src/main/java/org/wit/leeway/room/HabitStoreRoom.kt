package org.wit.leeway.room

import android.content.Context
import androidx.room.Room
import org.wit.leeway.models.HabitModel
import org.wit.leeway.models.HabitStore

class HabitStoreRoom(val context: Context) : HabitStore {

    var dao: HabitDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.habitDao()
    }

    override suspend fun findAll(): List<HabitModel> {
        return dao.findAll()
    }

    override suspend fun findById(id: Long): HabitModel? {
        return dao.findById(id)
    }

    override suspend fun create(habit: HabitModel) {
        dao.create(habit)
    }

    override suspend fun update(habit: HabitModel) {
        dao.update(habit)
    }

    override suspend fun delete(habit: HabitModel) {
        dao.deleteHabit(habit)
    }

    override suspend fun clear() {
    }
}