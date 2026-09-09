package com.example.offlinesqllearner

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.offlinesqllearner.db.PlaygroundDbHelper
import com.example.offlinesqllearner.db.SqlRunResult

class PlaygroundActivity : AppCompatActivity() {

    private lateinit var dbHelper: PlaygroundDbHelper
    private lateinit var etSql: EditText
    private lateinit var tvStatus: TextView
    private lateinit var resultTable: TableLayout

    private val sampleQueries = listOf(
        "SELECT * FROM employees;",
        "SELECT * FROM departments;",
        "SELECT name, salary FROM employees WHERE salary > 50000;",
        "SELECT dept_id, COUNT(*) FROM employees GROUP BY dept_id;",
        "SELECT e.name, d.dept_name FROM employees e JOIN departments d ON e.dept_id = d.id;",
        "SELECT * FROM products ORDER BY price DESC;"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playground)

        dbHelper = PlaygroundDbHelper(this)
        etSql = findViewById(R.id.etSql)
        tvStatus = findViewById(R.id.tvStatus)
        resultTable = findViewById(R.id.resultTable)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnRun).setOnClickListener { runQuery() }

        findViewById<View>(R.id.btnReset).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset sample database?")
                .setMessage("This restores employees, departments, products and orders to their original sample data.")
                .setPositiveButton("Reset") { _, _ ->
                    dbHelper.resetDatabase()
                    Toast.makeText(this, "Database reset ✓", Toast.LENGTH_SHORT).show()
                    clearResults()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        buildChips()

        val prefill = intent.getStringExtra("prefill_query")
        if (!prefill.isNullOrBlank()) {
            etSql.setText(prefill)
        }
    }

    private fun buildChips() {
        val chipContainer = findViewById<LinearLayout>(R.id.chipContainer)
        sampleQueries.forEach { query ->
            val chip = TextView(this)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = dp(8)
            params.topMargin = dp(4)
            params.bottomMargin = dp(4)
            chip.layoutParams = params
            chip.text = query.substringBefore(";").take(28) + if (query.length > 29) "…" else ""
            chip.setTextColor(resources.getColor(R.color.accent_blue, theme))
            chip.textSize = 11f
            chip.setPadding(dp(12), dp(7), dp(12), dp(7))
            chip.background = getDrawable(R.drawable.bg_pill)
            chip.setOnClickListener { etSql.setText(query) }
            chipContainer.addView(chip)
        }
    }

    private fun runQuery() {
        val sql = etSql.text.toString().trim()
        if (sql.isEmpty()) {
            Toast.makeText(this, "Type a SQL query first", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val result = dbHelper.runSql(sql)
            renderResult(result)
        } catch (e: Exception) {
            renderResult(SqlRunResult.Error(e.message ?: "Unknown SQL error"))
        }
    }

    private fun renderResult(result: SqlRunResult) {
        clearResults()
        when (result) {
            is SqlRunResult.Table -> {
                if (result.rows.isEmpty() && result.columns.isEmpty()) {
                    showStatus("Query ran successfully but returned no data.", R.color.text_secondary)
                    return
                }
                addHeaderRow(result.columns)
                if (result.rows.isEmpty()) {
                    showStatus("0 rows returned.", R.color.text_secondary)
                } else {
                    result.rows.forEach { row -> addDataRow(row) }
                    showStatus("${result.rows.size} row(s) returned.", R.color.accent_green)
                }
            }
            is SqlRunResult.Message -> showStatus(result.text, R.color.accent_green)
            is SqlRunResult.Error -> showStatus("⚠️ ${result.text}", R.color.accent_red)
        }
    }

    private fun addHeaderRow(columns: List<String>) {
        val row = TableRow(this)
        columns.forEach { col ->
            val tv = makeCell(col, isHeader = true)
            row.addView(tv)
        }
        resultTable.addView(row)
    }

    private fun addDataRow(values: List<String>) {
        val row = TableRow(this)
        values.forEach { value ->
            val tv = makeCell(value, isHeader = false)
            row.addView(tv)
        }
        resultTable.addView(row)
    }

    private fun makeCell(text: String, isHeader: Boolean): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.setPadding(dp(14), dp(10), dp(14), dp(10))
        tv.typeface = Typeface.MONOSPACE
        tv.textSize = 12f
        if (isHeader) {
            tv.setTextColor(Color.WHITE)
            tv.setBackgroundColor(resources.getColor(R.color.accent_purple, theme))
            tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        } else {
            tv.setTextColor(resources.getColor(R.color.text_primary, theme))
            tv.setBackgroundColor(resources.getColor(R.color.surface, theme))
        }
        val params = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, dp(2), dp(2))
        tv.layoutParams = params
        return tv
    }

    private fun clearResults() {
        resultTable.removeAllViews()
        tvStatus.visibility = View.GONE
    }

    private fun showStatus(text: String, colorRes: Int) {
        tvStatus.visibility = View.VISIBLE
        tvStatus.text = text
        tvStatus.setTextColor(resources.getColor(colorRes, theme))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
