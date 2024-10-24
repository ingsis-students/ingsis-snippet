package com.students.ingsissnippet.fixture

object UserFixtures {

    fun all(): List<PermissionDb> {
        return listOf(
            PermissionDb("example@gmail.com", mutableListOf(1, 2)),
            PermissionDb("jane@gmail.com", mutableListOf(3)),
            PermissionDb("robert@gmail.com", mutableListOf(4)),
            PermissionDb("julian@gmail.com", mutableListOf(5))
        )
    }

    // Data for eventual tests
// val KOTLIN_SNIPPET = Snippet(1, "Kotlin Basics", "fun main() { println(\"Hello, Kotlin!\") }", "Kotlin", "John Doe")
// val JAVA_SNIPPET = Snippet(2, "Java Hello World", "public class Main { public static void main(String[] args) { System.out.println(\"Hello, Java!\"); } }", "Java", "Jane Smith")
// val PYTHON_SNIPPET = Snippet(3, "Python Example", "print(\"Hello, Python!\")", "Python", "Alice Johnson")
// val JAVASCRIPT_SNIPPET = Snippet(4, "JavaScript Alert", "alert(\"Hello, JavaScript!\")", "JavaScript", "Bob Lee")
// val RUBY_SNIPPET = Snippet(5, "Ruby Hello World", "puts \"Hello, Ruby!\"", "Ruby", "Charlie Brown")
}
