package com.dvpro.kamen

import android.content.SharedPreferences

object Data {
    // Переменная для трекинга отслеживания, если -1, то отслеживание не запущено,
    // если не -1, то тут время начала, инициализируется при запуске
    var TrackingStatus: Long = -1

    //SharedPreferences для получения настроек, инициализируется при запуске
    var prefman : SharedPreferences? = null

    //Лимит ношения маски, в секундах, инициализируется при запуске
    fun getMaskWearLimit(): Int{
        return prefman?.getString("limit","120")!!.toInt()*60
    }

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