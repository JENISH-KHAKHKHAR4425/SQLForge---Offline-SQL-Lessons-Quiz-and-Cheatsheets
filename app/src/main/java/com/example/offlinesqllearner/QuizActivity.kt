package com.example.offlinesqllearner

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.offlinesqllearner.data.Question
import com.example.offlinesqllearner.data.QuestionRepo
import com.example.offlinesqllearner.util.Prefs

class QuizActivity : AppCompatActivity() {

    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private var score = 0
    private var answered = false

    private lateinit var tvQuestion: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvExplanation: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var btnNext: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvProgress = findViewById(R.id.tvProgress)
        tvScore = findViewById(R.id.tvScore)
        tvExplanation = findViewById(R.id.tvExplanation)
        optionsContainer = findViewById(R.id.optionsContainer)
        btnNext = findViewById(R.id.btnNext)
        progressBar = findViewById(R.id.progressBar)

        questions = QuestionRepo.randomSet(10)

        findViewById<View>(R.id.btnClose).setOnClickListener { confirmExit() }
        btnNext.setOnClickListener { goToNext() }

        renderQuestion()
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle("Quit quiz?")
            .setMessage("Your progress on this attempt will be lost.")
            .setPositiveButton("Quit") { _, _ -> finish() }
            .setNegativeButton("Keep going", null)
            .show()
    }

    private fun renderQuestion() {
        answered = false
        val q = questions[currentIndex]
        tvQuestion.text = q.prompt
        tvProgress.text = "Question ${currentIndex + 1} / ${questions.size}"
        tvScore.text = "Score: $score"
        progressBar.progress = ((currentIndex) * 100) / questions.size
        tvExplanation.visibility = View.GONE
        tvExplanation.text = ""
        btnNext.isEnabled = false
        btnNext.alpha = 0.5f
        btnNext.text = if (currentIndex == questions.size - 1) "Finish Quiz 🏁" else "Next Question →"

        optionsContainer.removeAllViews()
        q.options.forEachIndexed { idx, optionText ->
            val optionView = TextView(this)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(12)
            optionView.layoutParams = params
            optionView.text = optionText
            optionView.textSize = 14f
            optionView.setTextColor(getColor(R.color.text_primary))
            optionView.setPadding(dp(16), dp(14), dp(16), dp(14))
            optionView.background = getDrawable(R.drawable.bg_answer_default)
            optionView.gravity = Gravity.CENTER_VERTICAL
            optionView.isClickable = true
            optionView.isFocusable = true
            optionView.setOnClickListener { onOptionSelected(idx) }
            optionsContainer.addView(optionView)
        }
    }

    private fun onOptionSelected(selectedIndex: Int) {
        if (answered) return
        answered = true
        val q = questions[currentIndex]

        for (i in 0 until optionsContainer.childCount) {
            val child = optionsContainer.getChildAt(i) as TextView
            child.isClickable = false
            when {
                i == q.correctIndex -> child.background = getDrawable(R.drawable.bg_answer_correct)
                i == selectedIndex -> child.background = getDrawable(R.drawable.bg_answer_wrong)
            }
        }

        if (selectedIndex == q.correctIndex) {
            score++
            tvScore.text = "Score: $score"
        }

        tvExplanation.text = "💡 ${q.explanation}"
        tvExplanation.visibility = View.VISIBLE

        btnNext.isEnabled = true
        btnNext.alpha = 1f
    }

    private fun goToNext() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            renderQuestion()
        } else {
            Prefs.recordQuizResult(this, score, questions.size)
            val intent = Intent(this, QuizResultActivity::class.java)
            intent.putExtra("score", score)
            intent.putExtra("total", questions.size)
            startActivity(intent)
            finish()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
