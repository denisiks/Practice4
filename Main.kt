import java.util.*
import java.util.Scanner

data class Subject(var title: String, var grade: Int)

data class Student(val name: String?, val birthYear: Int, val subjects: List<Subject>) {

    val averageGrade
        get() = subjects.average { it.grade.toFloat() }
    val age
        get()= Calendar.getInstance().get(Calendar.YEAR) - birthYear

    override fun toString(): String =
        "Имя: $name, Год рождения: $birthYear, Список дисциплин: $subjects, Средняя оценка: $averageGrade"

}
fun <T> Iterable<T>.average(block: (T) -> Float): Float {
    var sum = 0.0
    var count = 0
    for (element in this) {
        sum += block(element)
        ++count
    }
    return (sum / count).toFloat()
}

data class University(val title: String, val students: MutableList<Student>) {

    val average
        get() = students.filter{ it.age in 17..21 }.average { it.averageGrade }

    val courses
        get() = students.groupBy { it.age }.mapKeys {
            when (it.key) {
                17 -> 1
                18 -> 2
                19 -> 3
                20 -> 4
                21 -> 5
                else -> throw StudentTooYoungException()
            }
        }
}

class StudentTooYoungException : Exception("Студент слишком молод")

enum class StudyProgram(private val title: String) {
    DISCIPLINE1("Алгебра и геометрия"), DISCIPLINE2("История"),
    DISCIPLINE3("Программирование"), DISCIPLINE4("Физика"),
    DISCIPLINE5("Обществознание");

    infix fun withGrade(grade: Int): Subject = Subject(title, grade)

}

typealias StudentListener = ((Student) -> Unit)

object DataSource {
    val university: University by lazy {
        University("МГУ им. Н.П. Огарёва", students)
    }

    var onNewStudentListener: StudentListener? = null

    fun addStudent(students: MutableList<Student>) {
        println("Добавление студента. Введите имя : ")
        val name = readLine()
        println("Введите год рождения : ")
        val year = Scanner(System.`in`)
        val birthYear: Int = year.nextInt()
        students.add(Student(name, birthYear, listOf(StudyProgram.DISCIPLINE3 withGrade 5, StudyProgram.DISCIPLINE5 withGrade 5)))
        val addedStudent = students.last()
        onNewStudentListener?.invoke(addedStudent)
    }
}

val students = mutableListOf(
    Student("Кирилл Николаев", 2001, listOf(StudyProgram.DISCIPLINE1 withGrade 5, StudyProgram.DISCIPLINE2 withGrade 5)),
    Student("Андрей Сараев", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 5, StudyProgram.DISCIPLINE2 withGrade 4)),
    Student("Дмитрий Федин", 2000, listOf(StudyProgram.DISCIPLINE4 withGrade 3, StudyProgram.DISCIPLINE5 withGrade 3)),
    Student("Анастасия Князева", 2001, listOf(StudyProgram.DISCIPLINE4 withGrade 5, StudyProgram.DISCIPLINE5 withGrade 4)),
    Student("Роман Коваленко", 2003, listOf(StudyProgram.DISCIPLINE1 withGrade 3, StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Семён Шлаев", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 5, StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Анна Жукова", 2004, listOf(StudyProgram.DISCIPLINE1 withGrade 4, StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Илья Быков", 2004, listOf(StudyProgram.DISCIPLINE2 withGrade 4, StudyProgram.DISCIPLINE4 withGrade 3))
)
fun main() {

    println("Универсистет: " + DataSource.university.title)
    println("\t" + DataSource.university.students.joinToString(separator = "\n\t"))
    println("Список студентов по курсам = " + DataSource.university.courses)
    println("Средняя оценка по университету = " + DataSource.university.average)

    DataSource.onNewStudentListener = {
        println("Новый студент: $it" + "\nСредняя оценка по университету: ${DataSource.university.average}")
    }
    DataSource.addStudent(students)

    println("Разбивка по курсам = " + DataSource.university.courses)
}
