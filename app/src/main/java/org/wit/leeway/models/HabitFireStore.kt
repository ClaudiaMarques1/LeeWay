package org.wit.leeway.models

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.wit.leeway.helpers.readImageFromPath
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File

class HabitFireStore(val context: Context) : HabitStore {
    val habits = ArrayList<HabitModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference
    override suspend fun findAll(): List<HabitModel> {
        return habits
    }

    override suspend fun findById(id: Long): HabitModel? {
        val foundHabit: HabitModel? = habits.find { h -> h.id == id }
        return foundHabit
    }

    override suspend fun create(habit: HabitModel) {
        val key = db.child("users").child(userId).child("habits").push().key
        key?.let {
            habit.fbId = key
            habits.add(habit)
            db.child("users").child(userId).child("habits").child(key).setValue(habit)
            updateImage(habit)
        }
    }

    override suspend fun update(habit: HabitModel) {
        var foundHabit: HabitModel? = habits.find { h -> h.fbId == habit.fbId }
        if (foundHabit != null) {
            foundHabit.title = habit.title
            foundHabit.description = habit.description
            foundHabit.image = habit.image
            foundHabit.location = habit.location
        }

        db.child("users").child(userId).child("habits").child(habit.fbId).setValue(habit)
        if(habit.image.length > 0){
            updateImage(habit)
        }
    }

    override suspend fun delete(habit: HabitModel) {
        db.child("users").child(userId).child("habits").child(habit.fbId).removeValue()
        habits.remove(habit)
    }

    override suspend fun clear() {
        habits.clear()
    }

    fun fetchHabits(habitsReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(habits) {
                    it.getValue<HabitModel>(
                        HabitModel::class.java
                    )
                }
                habitsReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        st = FirebaseStorage.getInstance().reference
        db = FirebaseDatabase.getInstance("https://leeway-mad-default-rtdb.us-central1.firebasedatabase.app").reference
        habits.clear()
        db.child("users").child(userId).child("habits")
            .addListenerForSingleValueEvent(valueEventListener)
    }

    fun updateImage(habit: HabitModel) {
        if (habit.image != "") {
            val fileName = File(habit.image)
            val imageName = fileName.getName()

            var imageRef = st.child(userId + '/' + imageName)
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, habit.image)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)

                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        habit.image = it.toString()
                        db.child("users").child(userId).child("habits").child(habit.fbId).setValue(habit)
                    }
                }.addOnFailureListener{
                    var errorMessage = it.message
                    Timber.i("Failure: $errorMessage")
                }
            }
        }
    }
}