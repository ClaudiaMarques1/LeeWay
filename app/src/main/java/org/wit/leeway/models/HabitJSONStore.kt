package org.wit.leeway.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.leeway.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "habits.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<HabitModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class HabitJSONStore(private val context: Context) : HabitStore {

    var habits = mutableListOf<HabitModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override suspend fun findAll(): MutableList<HabitModel> {
        logAll()
        return habits
    }

    override suspend fun create(habit: HabitModel) {
        habit.id = generateRandomId()
        habits.add(habit)
        serialize()
    }


    override suspend fun update(habit: HabitModel) {
        val habitsList = findAll() as ArrayList<HabitModel>
        var foundHabit: HabitModel? = habitsList.find { h -> h.id == habit.id }
        if (foundHabit != null) {
            foundHabit.title = habit.title
            foundHabit.description = habit.description
            foundHabit.image = habit.image
            foundHabit.location = habit.location
        }
        serialize()
    }

    override suspend fun delete(habit: HabitModel) {
        val foundHabit: HabitModel? = habits.find { it.id == habit.id }
        habits.remove(foundHabit)
        serialize()
    }
    override suspend fun findById(id:Long) : HabitModel? {
        val foundHabit: HabitModel? = habits.find { it.id == id }
        return foundHabit
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(habits, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        habits = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        habits.forEach { Timber.i("$it") }
    }

    override suspend fun clear(){
        habits.clear()
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}