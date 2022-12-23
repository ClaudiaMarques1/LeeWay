package org.wit.leeway.room

import androidx.room.*
import org.wit.leeway.models.HabitModel

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(habit: HabitModel)

    @Query("SELECT * FROM HabitModel")
    suspend fun findAll(): List<HabitModel>

    @Query("select * from HabitModel where id = :id")
    suspend fun findById(id: Long): HabitModel

    @Update
    suspend fun update(habit: HabitModel)

    @Delete
    suspend fun deleteHabit(habit: HabitModel)
}