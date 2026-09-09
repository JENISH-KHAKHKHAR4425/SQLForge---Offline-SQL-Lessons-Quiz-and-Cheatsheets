package com.example.offlinesqllearner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.offlinesqllearner.data.LessonRepo
import com.example.offlinesqllearner.util.Prefs

class LessonDetailActivity : AppCompatActivity() {

    private var lessonId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_detail)

        lessonId = intent.getIntExtra("lesson_id", 1)
        bind()

        findViewById<android.view.View>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.btnRunInPlayground).setOnClickListener {
            val lesson = LessonRepo.byId(lessonId) ?: return@setOnClickListener
            val intent = Intent(this, PlaygroundActivity::class.java)
            intent.putExtra("prefill_query", lesson.sampleQuery)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnMarkComplete).setOnClickListener {
            Prefs.markLessonComplete(this, lessonId)
            Toast.makeText(this, "Marked as complete ✓", Toast.LENGTH_SHORT).show()
            bind()
        }

        findViewById<TextView>(R.id.btnNextLesson).setOnClickListener {
            val next = LessonRepo.byId(lessonId + 1)
            if (next != null) {
                val intent = Intent(this, LessonDetailActivity::class.java)
                intent.putExtra("lesson_id", next.id)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "🎉 You've reached the last lesson!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bind() {
        val lesson = LessonRepo.byId(lessonId) ?: return
        findViewById<TextView>(R.id.tvEmoji).text = lesson.emoji
        findViewById<TextView>(R.id.tvTitle).text = lesson.title
        findViewById<TextView>(R.id.tvContent).text = lesson.content
        findViewById<TextView>(R.id.tvSampleQuery).text = lesson.sampleQuery
        findViewById<TextView>(R.id.tvDifficultyBadge).text = lesson.difficulty

        val completeBtn = findViewById<TextView>(R.id.btnMarkComplete)
        if (Prefs.isLessonComplete(this, lessonId)) {
            completeBtn.text = "Completed ✓"
            completeBtn.alpha = 0.7f
        } else {
            completeBtn.text = "Mark Complete ✓"
            completeBtn.alpha = 1f
        }

        val nextBtn = findViewById<TextView>(R.id.btnNextLesson)
        nextBtn.text = if (LessonRepo.byId(lessonId + 1) != null) "Next Lesson →" else "🎉 Last Lesson"
    }
}
