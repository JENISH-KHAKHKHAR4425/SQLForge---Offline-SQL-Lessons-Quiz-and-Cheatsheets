package com.example.offlinesqllearner.data

object LessonRepo {

    val lessons: List<Lesson> = listOf(
        Lesson(
            id = 1,
            title = "Databases & SQL Basics",
            emoji = "🗄️",
            difficulty = "Beginner",
            summary = "What a database is, and how SQL talks to it.",
            content = """
A database is an organized collection of data stored in tables. Each table has rows (records) and columns (fields).

SQL (Structured Query Language) is the standard language used to create, read, update and delete data inside a relational database like SQLite, MySQL, PostgreSQL or SQL Server.

The five main categories of SQL commands are:
• DDL (Data Definition) — CREATE, ALTER, DROP
• DML (Data Manipulation) — SELECT, INSERT, UPDATE, DELETE
• DCL (Data Control) — GRANT, REVOKE
• TCL (Transaction Control) — COMMIT, ROLLBACK
• DQL (Data Query) — SELECT

Every SQL statement ends with a semicolon ( ; ). SQL keywords are not case-sensitive, but it is best practice to write them in UPPERCASE to separate them from table and column names.

This app comes with a sample database preloaded in the Playground so you can try every command yourself, completely offline.
            """.trimIndent(),
            sampleQuery = "SELECT * FROM employees;"
        ),
        Lesson(
            id = 2,
            title = "SELECT Statement",
            emoji = "🔍",
            difficulty = "Beginner",
            summary = "Retrieve data from one or more columns.",
            content = """
SELECT is the most used SQL command — it reads data from a table.

Basic syntax:
SELECT column1, column2 FROM table_name;

To select every column, use the wildcard:
SELECT * FROM employees;

You can rename a column in the output using an alias:
SELECT name AS full_name FROM employees;

You can also select without a table, useful for testing expressions:
SELECT 5 * 3;

Tip: SELECT never changes your data — it only reads it, which makes it perfectly safe to experiment with.
            """.trimIndent(),
            sampleQuery = "SELECT name, salary FROM employees;"
        ),
        Lesson(
            id = 3,
            title = "WHERE & Filtering",
            emoji = "🎯",
            difficulty = "Beginner",
            summary = "Narrow results down with conditions.",
            content = """
WHERE filters rows based on a condition:
SELECT * FROM employees WHERE salary > 50000;

Comparison operators: =, !=, <, >, <=, >=

Combine conditions with AND / OR:
SELECT * FROM employees WHERE dept_id = 1 AND salary > 40000;

Useful keywords:
• BETWEEN 30000 AND 60000
• IN (1, 2, 3)
• LIKE 'A%'  (pattern matching — % means any characters, _ means one character)
• IS NULL / IS NOT NULL

Example:
SELECT * FROM employees WHERE name LIKE 'A%';
            """.trimIndent(),
            sampleQuery = "SELECT * FROM employees WHERE salary > 50000;"
        ),
        Lesson(
            id = 4,
            title = "ORDER BY & LIMIT",
            emoji = "↕️",
            difficulty = "Beginner",
            summary = "Sort and cap the number of results.",
            content = """
ORDER BY sorts rows by one or more columns.

SELECT * FROM employees ORDER BY salary DESC;

ASC = ascending (default), DESC = descending.

Sort by multiple columns:
SELECT * FROM employees ORDER BY dept_id ASC, salary DESC;

LIMIT restricts how many rows come back — great for "Top N" queries:
SELECT * FROM employees ORDER BY salary DESC LIMIT 3;

Combine with OFFSET to page through results:
SELECT * FROM employees LIMIT 5 OFFSET 5;
            """.trimIndent(),
            sampleQuery = "SELECT * FROM employees ORDER BY salary DESC LIMIT 3;"
        ),
        Lesson(
            id = 5,
            title = "INSERT, UPDATE, DELETE",
            emoji = "✏️",
            difficulty = "Beginner",
            summary = "Add, change, and remove rows of data.",
            content = """
INSERT adds a new row:
INSERT INTO employees (name, dept_id, salary) VALUES ('Zara', 2, 55000);

UPDATE changes existing rows — always use WHERE, or every row is changed!
UPDATE employees SET salary = 60000 WHERE name = 'Zara';

DELETE removes rows — again, always use WHERE:
DELETE FROM employees WHERE name = 'Zara';

⚠️ Running UPDATE or DELETE without a WHERE clause affects the entire table. Try it safely in the Playground — you can always reset the sample database.
            """.trimIndent(),
            sampleQuery = "INSERT INTO employees (name, dept_id, salary) VALUES ('Zara', 2, 55000);"
        ),
        Lesson(
            id = 6,
            title = "Data Types & Constraints",
            emoji = "🧱",
            difficulty = "Beginner",
            summary = "Shape and protect your table's data.",
            content = """
Common data types: INTEGER, TEXT, REAL, NUMERIC, BLOB (SQLite is flexible about types).

Constraints protect data integrity:
• PRIMARY KEY — uniquely identifies each row
• NOT NULL — the column can't be empty
• UNIQUE — no duplicate values allowed
• DEFAULT — a fallback value if none is given
• CHECK — validates a rule, e.g. CHECK(salary > 0)

Example table definition:
CREATE TABLE employees (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  salary REAL DEFAULT 0,
  dept_id INTEGER
);
            """.trimIndent(),
            sampleQuery = "CREATE TABLE demo (id INTEGER PRIMARY KEY, name TEXT NOT NULL);"
        ),
        Lesson(
            id = 7,
            title = "Primary & Foreign Keys",
            emoji = "🔑",
            difficulty = "Intermediate",
            summary = "Link tables together correctly.",
            content = """
A PRIMARY KEY uniquely identifies each row in a table — no two rows can share the same value.

A FOREIGN KEY is a column that references the primary key of another table, creating a relationship between them.

Example:
CREATE TABLE departments (
  id INTEGER PRIMARY KEY,
  dept_name TEXT
);

CREATE TABLE employees (
  id INTEGER PRIMARY KEY,
  name TEXT,
  dept_id INTEGER,
  FOREIGN KEY (dept_id) REFERENCES departments(id)
);

Foreign keys are how relational databases stay "relational" — they let you join related data together without duplicating it.
            """.trimIndent(),
            sampleQuery = "SELECT e.name, d.dept_name FROM employees e, departments d WHERE e.dept_id = d.id;"
        ),
        Lesson(
            id = 8,
            title = "JOINs",
            emoji = "🔗",
            difficulty = "Intermediate",
            summary = "Combine rows from two or more tables.",
            content = """
JOIN combines rows from two tables based on a related column.

INNER JOIN — only rows that match in both tables:
SELECT e.name, d.dept_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.id;

LEFT JOIN — all rows from the left table, matched rows from the right (NULL if no match):
SELECT e.name, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id;

SQLite doesn't support RIGHT JOIN or FULL JOIN directly, but a RIGHT JOIN can be simulated by swapping table order in a LEFT JOIN.

Aliases (e, d) keep the query short and readable.
            """.trimIndent(),
            sampleQuery = "SELECT e.name, d.dept_name FROM employees e LEFT JOIN departments d ON e.dept_id = d.id;"
        ),
        Lesson(
            id = 9,
            title = "GROUP BY & Aggregates",
            emoji = "📊",
            difficulty = "Intermediate",
            summary = "Summarize data with functions like COUNT and AVG.",
            content = """
Aggregate functions summarize many rows into one value:
COUNT(), SUM(), AVG(), MIN(), MAX()

GROUP BY groups rows that share a value, so aggregates apply per group:
SELECT dept_id, COUNT(*) AS total, AVG(salary) AS avg_salary
FROM employees
GROUP BY dept_id;

HAVING filters groups AFTER aggregation (WHERE can't be used here because the group doesn't exist yet when filtering happens):
SELECT dept_id, COUNT(*) AS total
FROM employees
GROUP BY dept_id
HAVING COUNT(*) > 1;
            """.trimIndent(),
            sampleQuery = "SELECT dept_id, COUNT(*) AS total, AVG(salary) AS avg_salary FROM employees GROUP BY dept_id;"
        ),
        Lesson(
            id = 10,
            title = "Subqueries",
            emoji = "🪆",
            difficulty = "Intermediate",
            summary = "A query nested inside another query.",
            content = """
A subquery is a SELECT statement nested inside another statement, usually inside parentheses.

Find employees earning more than the average salary:
SELECT name, salary FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);

Subqueries can also appear in the FROM clause (a "derived table") or after IN:
SELECT name FROM employees
WHERE dept_id IN (SELECT id FROM departments WHERE dept_name = 'Engineering');

Correlated subqueries reference the outer query's columns and run once per outer row — powerful, but slower on large data.
            """.trimIndent(),
            sampleQuery = "SELECT name, salary FROM employees WHERE salary > (SELECT AVG(salary) FROM employees);"
        ),
        Lesson(
            id = 11,
            title = "Views",
            emoji = "🪟",
            difficulty = "Intermediate",
            summary = "Save a query as a virtual, reusable table.",
            content = """
A VIEW is a saved SELECT query that behaves like a virtual table. It doesn't store data itself — it re-runs the underlying query every time you use it.

CREATE VIEW high_earners AS
SELECT name, salary FROM employees WHERE salary > 50000;

Then query the view exactly like a table:
SELECT * FROM high_earners;

Views are great for hiding complexity, enforcing consistent business logic, and controlling what columns are exposed.

Drop a view with:
DROP VIEW high_earners;
            """.trimIndent(),
            sampleQuery = "CREATE VIEW high_earners AS SELECT name, salary FROM employees WHERE salary > 50000;"
        ),
        Lesson(
            id = 12,
            title = "Indexes & Performance",
            emoji = "⚡",
            difficulty = "Advanced",
            summary = "Speed up queries on large tables.",
            content = """
An INDEX is a data structure that speeds up lookups on a column, much like the index at the back of a book.

CREATE INDEX idx_salary ON employees(salary);

Without an index, the database scans every row (a "full table scan") to find matches. With an index, it can jump straight to the relevant rows.

Trade-offs:
• Faster SELECT / WHERE / JOIN / ORDER BY on indexed columns
• Slightly slower INSERT / UPDATE / DELETE, since the index must also be updated
• Extra storage space

Index columns that are frequently searched or joined on — usually primary keys and foreign keys are indexed automatically.
            """.trimIndent(),
            sampleQuery = "CREATE INDEX idx_salary ON employees(salary);"
        ),
        Lesson(
            id = 13,
            title = "Transactions (ACID)",
            emoji = "🔒",
            difficulty = "Advanced",
            summary = "Group statements so they succeed or fail together.",
            content = """
A transaction bundles multiple statements into a single unit of work.

BEGIN TRANSACTION;
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;
COMMIT;

If anything goes wrong, ROLLBACK undoes every change made since BEGIN.

Transactions guarantee the ACID properties:
• Atomicity — all statements succeed, or none do
• Consistency — the database moves from one valid state to another
• Isolation — concurrent transactions don't interfere with each other
• Durability — once committed, changes survive a crash

This is essential for anything involving money, inventory, or any operation where a partial update would be dangerous.
            """.trimIndent(),
            sampleQuery = "BEGIN TRANSACTION; UPDATE employees SET salary = salary + 1000 WHERE dept_id = 1; COMMIT;"
        ),
        Lesson(
            id = 14,
            title = "CTEs & Window Functions",
            emoji = "🧮",
            difficulty = "Advanced",
            summary = "Modern SQL for readable, powerful queries.",
            content = """
A Common Table Expression (CTE) is a named, temporary result set defined with WITH — it makes complex queries far more readable.

WITH dept_avg AS (
  SELECT dept_id, AVG(salary) AS avg_sal FROM employees GROUP BY dept_id
)
SELECT e.name, e.salary, d.avg_sal
FROM employees e
JOIN dept_avg d ON e.dept_id = d.dept_id;

Window functions perform a calculation across a set of rows related to the current row, without collapsing them like GROUP BY does:

SELECT name, salary,
  RANK() OVER (ORDER BY salary DESC) AS salary_rank
FROM employees;

Modern SQLite (3.25+) supports both CTEs and window functions — try them out in the Playground!
            """.trimIndent(),
            sampleQuery = "SELECT name, salary, RANK() OVER (ORDER BY salary DESC) AS salary_rank FROM employees;"
        )
    )

    fun byId(id: Int): Lesson? = lessons.find { it.id == id }
}
