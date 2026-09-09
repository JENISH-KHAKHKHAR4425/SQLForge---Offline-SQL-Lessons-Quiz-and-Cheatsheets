package com.example.offlinesqllearner.data

data class Lesson(
    val id: Int,
    val title: String,
    val emoji: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val summary: String,
    val content: String,
    val sampleQuery: String
)

data class Question(
    val id: Int,
    val lessonId: Int,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
