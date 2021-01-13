package com.dvpro.kamen

object Data {
    // Переменная для трекинга отслеживания, если -1, то отслеживание не запущено,
    // если не -1, то тут время начала
    var TrackingStatus: Long = -1;

    /* Выбирает правильную форму существительного в зависимости от числа.
    один-два-пять - один гвоздь, два гвоздя, пять гвоздей.
    in: число и слово в трёх падежах.
    out: строка (число + существительное в нужном падеже).
    */
    fun choosePluralMerge(num: Long, caseOne: String, caseTwo: String, caseFive: String): String? {
        var str: String? = "$num "
        val number = kotlin.math.abs(num)
        str += if (number % 10 == 1L && number % 100 != 11L) {
            caseOne
        } else if (number % 10 in 2..4 && (number % 100 < 10 || number % 100 >= 20)) {
            caseTwo
        } else {
            caseFive
        }
        return str
    }
}