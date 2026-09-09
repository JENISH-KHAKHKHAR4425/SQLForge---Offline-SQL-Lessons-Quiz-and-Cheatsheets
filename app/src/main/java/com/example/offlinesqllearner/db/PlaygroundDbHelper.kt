package com.example.offlinesqllearner.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * A real, writable SQLite database that ships with sample data so users can
 * run ANY SQL statement (SELECT/INSERT/UPDATE/DELETE/CREATE/DROP/etc.)
 * completely offline, with zero network access required.
 */
class PlaygroundDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "playground.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        seed(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS employees")
        db.execSQL("DROP TABLE IF EXISTS departments")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS orders")
        seed(db)
    }

    private fun seed(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE departments (
                id INTEGER PRIMARY KEY,
                dept_name TEXT NOT NULL
            );
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE employees (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                dept_id INTEGER,
                salary REAL,
                FOREIGN KEY (dept_id) REFERENCES departments(id)
            );
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE products (
                id INTEGER PRIMARY KEY,
                product_name TEXT NOT NULL,
                price REAL,
                stock INTEGER
            );
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE orders (
                id INTEGER PRIMARY KEY,
                product_id INTEGER,
                employee_id INTEGER,
                quantity INTEGER,
                FOREIGN KEY (product_id) REFERENCES products(id),
                FOREIGN KEY (employee_id) REFERENCES employees(id)
            );
            """.trimIndent()
        )

        val depts = listOf("Engineering", "Sales", "Marketing", "Support")
        depts.forEachIndexed { i, name ->
            db.execSQL("INSERT INTO departments (id, dept_name) VALUES (${i + 1}, '$name');")
        }

        val employees = listOf(
            Triple("Aarav Sharma", 1, 72000.0),
            Triple("Priya Patel", 1, 68000.0),
            Triple("Rohan Mehta", 2, 51000.0),
            Triple("Ishita Verma", 2, 47000.0),
            Triple("Kabir Singh", 3, 39000.0),
            Triple("Ananya Rao", 3, 42000.0),
            Triple("Dev Kapoor", 4, 33000.0),
            Triple("Sneha Nair", 1, 81000.0)
        )
        employees.forEachIndexed { i, (name, dept, salary) ->
            db.execSQL("INSERT INTO employees (id, name, dept_id, salary) VALUES (${i + 1}, '$name', $dept, $salary);")
        }

        val products = listOf(
            Triple("Wireless Mouse", 799.0, 120),
            Triple("Mechanical Keyboard", 3499.0, 45),
            Triple("USB-C Hub", 1299.0, 80),
            Triple("Monitor Stand", 999.0, 30),
            Triple("Webcam 1080p", 2199.0, 60)
        )
        products.forEachIndexed { i, (name, price, stock) ->
            db.execSQL("INSERT INTO products (id, product_name, price, stock) VALUES (${i + 1}, '$name', $price, $stock);")
        }

        val orders = listOf(
            Triple(1, 1, 3),
            Triple(2, 2, 1),
            Triple(3, 3, 5),
            Triple(1, 4, 2),
            Triple(4, 5, 1),
            Triple(2, 6, 4)
        )
        orders.forEachIndexed { i, (productId, employeeId, qty) ->
            db.execSQL("INSERT INTO orders (id, product_id, employee_id, quantity) VALUES (${i + 1}, $productId, $employeeId, $qty);")
        }
    }

    fun resetDatabase() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS orders")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS employees")
        db.execSQL("DROP TABLE IF EXISTS departments")
        seed(db)
    }

    /** Executes arbitrary SQL. Supports multiple ; separated statements. Returns the last SELECT's cursor result, or a status message. */
    fun runSql(sql: String): SqlRunResult {
        val db = writableDatabase
        val statements = sql.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        if (statements.isEmpty()) return SqlRunResult.Message("Nothing to run.")

        var lastSelectResult: SqlRunResult.Table? = null
        var rowsAffectedTotal = 0
        var statementCount = 0

        for (stmt in statements) {
            val isQuery = stmt.trim().let {
                it.startsWith("select", true) || it.startsWith("pragma", true) || it.startsWith("with", true)
            }
            if (isQuery) {
                val cursor: Cursor = db.rawQuery(stmt, null)
                lastSelectResult = cursorToTable(cursor)
                cursor.close()
            } else {
                db.execSQL(stmt)
                statementCount++
            }
        }

        return lastSelectResult ?: SqlRunResult.Message(
            if (statementCount > 0) "✅ Success — $statementCount statement(s) executed."
            else "✅ Done."
        )
    }

    private fun cursorToTable(cursor: Cursor): SqlRunResult.Table {
        val columns = cursor.columnNames.toList()
        val rows = mutableListOf<List<String>>()
        while (cursor.moveToNext()) {
            val row = (0 until cursor.columnCount).map { idx ->
                cursor.getString(idx) ?: "NULL"
            }
            rows.add(row)
        }
        return SqlRunResult.Table(columns, rows)
    }
}

sealed class SqlRunResult {
    data class Table(val columns: List<String>, val rows: List<List<String>>) : SqlRunResult()
    data class Message(val text: String) : SqlRunResult()
    data class Error(val text: String) : SqlRunResult()
}
