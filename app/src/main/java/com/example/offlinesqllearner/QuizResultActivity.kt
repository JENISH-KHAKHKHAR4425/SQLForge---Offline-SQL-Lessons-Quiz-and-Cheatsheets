package com.example.offlinesqllearner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.offlinesqllearner.util.Prefs

class QuizResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 1)
        val percent = if (total > 0) (score * 100 / total) else 0

        val (emoji, title) = when {
            percent >= 90 -> "🏆" to "SQL Master!"
            percent >= 70 -> "🎉" to "Great Job!"
            percent >= 50 -> "👍" to "Good Effort!"
            else -> "📚" to "Keep Practicing!"
        }

        findViewById<TextView>(R.id.tvEmojiResult).text = emoji
        findViewById<TextView>(R.id.tvResultTitle).text = title
        findViewById<TextView>(R.id.tvResultScore).text = "$score / $total correct ($percent%)"

        val best = Prefs.getBestScore(this)
        findViewById<TextView>(R.id.tvResultSub).text =
            if (percent >= best) "🔥 New personal best!" else "Your best score is $best%. Keep going!"

        findViewById<TextView>(R.id.btnRetry).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.btnHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}
