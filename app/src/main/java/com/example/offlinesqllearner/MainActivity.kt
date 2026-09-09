package com.example.offlinesqllearner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.offlinesqllearner.data.LessonRepo
import com.example.offlinesqllearner.util.Prefs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<android.view.View>(R.id.cardLessons).setOnClickListener {
            startActivity(Intent(this, LessonListActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardQuiz).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardPlayground).setOnClickListener {
            startActivity(Intent(this, PlaygroundActivity::class.java))
        }
        findViewById<TextView>(R.id.tvResetProgress).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset progress?")
                .setMessage("This clears your lesson completion, streak and best quiz score. This can't be undone.")
                .setPositiveButton("Reset") { _, _ ->
                    getSharedPreferences("query_craft_prefs", MODE_PRIVATE).edit().clear().apply()
                    refreshStats()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStats()
    }

    private fun refreshStats() {
        findViewById<TextView>(R.id.tvStreak).text = "🔥 ${Prefs.getStreak(this)}"
        findViewById<TextView>(R.id.tvBestScore).text = "🏆 ${Prefs.getBestScore(this)}%"
        val done = Prefs.getCompletedLessons(this).size
        findViewById<TextView>(R.id.tvLessonsDone).text = "📘 $done/${LessonRepo.lessons.size}"
    }
}
