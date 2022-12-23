package org.wit.leeway.models

interface HabitStore {
    suspend fun findAll(): List<HabitModel>
    suspend fun create(habit: HabitModel)
    suspend fun update(habit: HabitModel)
    suspend fun findById(id:Long) : HabitModel?
    suspend fun delete(habit: HabitModel)
    suspend fun clear()
}