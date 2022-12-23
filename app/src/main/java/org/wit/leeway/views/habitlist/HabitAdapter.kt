package org.wit.leeway.views.habitlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.leeway.databinding.CardHabitBinding
import org.wit.leeway.models.HabitModel

interface HabitListener {
    fun onHabitClick(habit: HabitModel)
}

class HabitAdapter constructor(private var habits: List<HabitModel>,
                                   private val listener: HabitListener) :
        RecyclerView.Adapter<HabitAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardHabitBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val habit = habits[holder.adapterPosition]
        holder.bind(habit, listener)
    }

    override fun getItemCount(): Int = habits.size

    class MainHolder(private val binding : CardHabitBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: HabitModel, listener: HabitListener) {
            binding.habitTitle.text = habit.title
            binding.description.text = habit.description
            if (habit.image != ""){
                Picasso.get()
                    .load(habit.image)
                    .resize(200, 200)
                    .into(binding.imageIcon)
            }
            binding.root.setOnClickListener { listener.onHabitClick(habit) }
        }
    }
}
