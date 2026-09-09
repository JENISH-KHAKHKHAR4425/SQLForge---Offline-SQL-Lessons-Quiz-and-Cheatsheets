package com.example.offlinesqllearner.util

import android.content.Context

object Prefs {
    private const val FILE = "query_craft_prefs"
    private const val KEY_COMPLETED = "completed_lessons"
    private const val KEY_BEST_SCORE = "best_score"
    private const val KEY_QUIZZES_TAKEN = "quizzes_taken"
    private const val KEY_STREAK = "streak"
    private const val KEY_LAST_DAY = "last_active_day"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun markLessonComplete(ctx: Context, lessonId: Int) {
        val set = getCompletedLessons(ctx).toMutableSet()
        set.add(lessonId)
        prefs(ctx).edit().putStringSet(KEY_COMPLETED, set.map { it.toString() }.toSet()).apply()
        touchStreak(ctx)
    }

    fun isLessonComplete(ctx: Context, lessonId: Int): Boolean =
        getCompletedLessons(ctx).contains(lessonId)

    fun getCompletedLessons(ctx: Context): Set<Int> =
        prefs(ctx).getStringSet(KEY_COMPLETED, emptySet())!!.mapNotNull { it.toIntOrNull() }.toSet()

    fun recordQuizResult(ctx: Context, score: Int, total: Int) {
        val p = prefs(ctx)
        val best = p.getInt(KEY_BEST_SCORE, 0)
        val percent = if (total > 0) (score * 100 / total) else 0
        val editor = p.edit()
        if (percent > best) editor.putInt(KEY_BEST_SCORE, percent)
        editor.putInt(KEY_QUIZZES_TAKEN, p.getInt(KEY_QUIZZES_TAKEN, 0) + 1)
        editor.apply()
        touchStreak(ctx)
    }

    fun getBestScore(ctx: Context): Int = prefs(ctx).getInt(KEY_BEST_SCORE, 0)
    fun getQuizzesTaken(ctx: Context): Int = prefs(ctx).getInt(KEY_QUIZZES_TAKEN, 0)
    fun getStreak(ctx: Context): Int = prefs(ctx).getInt(KEY_STREAK, 0)

    private fun touchStreak(ctx: Context) {
        val p = prefs(ctx)
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val lastDay = p.getLong(KEY_LAST_DAY, -1)
        val editor = p.edit()
        when (today - lastDay) {
            0L -> { /* already counted today */ }
            1L -> editor.putInt(KEY_STREAK, p.getInt(KEY_STREAK, 0) + 1)
            else -> editor.putInt(KEY_STREAK, 1)
        }
        editor.putLong(KEY_LAST_DAY, today)
        editor.apply()
    }
}
