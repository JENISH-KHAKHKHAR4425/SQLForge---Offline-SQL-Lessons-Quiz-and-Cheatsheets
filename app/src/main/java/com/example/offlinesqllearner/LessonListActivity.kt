package com.example.offlinesqllearner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offlinesqllearner.adapter.LessonAdapter
import com.example.offlinesqllearner.data.LessonRepo
import com.example.offlinesqllearner.util.Prefs

class LessonListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_list)

        findViewById<android.view.View>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.rvLessons)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadList()
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }

    private fun loadList() {
        recyclerView.adapter = LessonAdapter(
            lessons = LessonRepo.lessons,
            isComplete = { id -> Prefs.isLessonComplete(this, id) },
            onClick = { lesson ->
                val intent = Intent(this, LessonDetailActivity::class.java)
                intent.putExtra("lesson_id", lesson.id)
                startActivity(intent)
            }
        )
    }
}
