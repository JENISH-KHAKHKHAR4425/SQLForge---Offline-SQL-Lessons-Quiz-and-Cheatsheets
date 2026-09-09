package com.example.offlinesqllearner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offlinesqllearner.R
import com.example.offlinesqllearner.data.Lesson

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val isComplete: (Int) -> Boolean,
    private val onClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emoji: TextView = view.findViewById(R.id.tvEmoji)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val summary: TextView = view.findViewById(R.id.tvSummary)
        val difficulty: TextView = view.findViewById(R.id.tvDifficulty)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val number: TextView = view.findViewById(R.id.tvNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(v)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.emoji.text = lesson.emoji
        holder.title.text = lesson.title
        holder.summary.text = lesson.summary
        holder.difficulty.text = lesson.difficulty
        holder.number.text = String.format("%02d", lesson.id)
        val complete = isComplete(lesson.id)
        holder.status.text = if (complete) "✓ Done" else "Start →"
        holder.itemView.setOnClickListener { onClick(lesson) }
    }

    override fun getItemCount(): Int = lessons.size
}
