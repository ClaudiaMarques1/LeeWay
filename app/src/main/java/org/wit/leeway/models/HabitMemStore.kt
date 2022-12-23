package org.wit.leeway.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class HabitMemStore : HabitStore {

    val habits = ArrayList<HabitModel>()

    override suspend fun findAll(): List<HabitModel> {
        return habits
    }

    override suspend fun create(habit: HabitModel) {
        habit.id = getId()
        habits.add(habit)
        logAll()
    }

    override suspend fun update(habit: HabitModel) {
        val foundHabit: HabitModel? = habits.find { h -> h.id == habit.id }
        if (foundHabit != null) {
            foundHabit.title = habit.title
            foundHabit.description = habit.description
            foundHabit.image = habit.image
            foundHabit.location = habit.location
            logAll()
        }
    }
    override suspend fun delete(habit: HabitModel) {
        habits.remove(habit)
        logAll()
    }

    private fun logAll() {
        habits.forEach { i("$it") }
    }

    override suspend fun findById(id:Long) : HabitModel? {
        val foundHabit: HabitModel? = habits.find { it.id == id }
        return foundHabit
    }

    override suspend fun clear(){
        habits.clear()
    }

}