package com.dvpro.kamen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

object Data {
    // Переменная для трекинга отслеживания, если -1, то отслеживание не запущено,
    // если не -1, то тут время начала, инициализируется при запуске
    var TrackingStatus: Long = -1

    //Изношенность текущей выбранной маски
    var CurMaskWear: Long = 0

    //SharedPreferences для получения настроек, инициализируется при запуске
    var prefman : SharedPreferences? = null

    //Лимит ношения маски, в секундах, инициализируется при запуске
    fun getMaskWearLimit(): Int{
        return prefman?.getString("limit", "120")!!.toInt()*60
    }

    //Таймер отсчёта
    var curtimer: Timer? = null

    var sp : SharedPreferences? = null

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











    @RequiresApi(Build.VERSION_CODES.O)
    fun Base_create(days: Int, context: Context, last_day_val: Int = 0){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE).edit()
        sp.clear()
        val t =getDaysAgo(0)
        sp.putString(t, last_day_val.toString())
        for (i in 2..days){
            sp.putString(getDaysAgo(i - 1), "0")
        }
        val d = LocalDate.now()
        sp.putString("last_day", d.toString()).apply()
    }






















    @RequiresApi(Build.VERSION_CODES.O)
    fun Statistic_update(time: Int, context: Context, days: Int = 30){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)

                val sp1 = sp.edit()


        val calendar = Calendar.getInstance()

        val j = 5L//Date_Check(context)
        val i =2

        if(j == 0L){
            val entries = arrayListOf<String>()
            for(i in 1 .. days-1){
                val t =getDaysAgo(i).toString()
                val k =sp.getString(t, null)
                entries.add(k.toString())
            }
                     val t =getDaysAgo(0)
            val k =sp.getString(t, null)
            val p =sp.getString("last_day", null)
            sp.edit().clear().apply()

            if(k==null){
                sp1.putString(t, time.toString())
            }
            else{
                val k1= ((k!!.toInt()) + time).toString()
                sp1.putString(t, k1)
            }

            for(i in 1 .. days-1){
                val t =getDaysAgo(i).toString()
               sp1.putString(t, entries[i-1])
            }



            sp1.putString("last_day",p).apply()
        }





        else if(j < days){
            for(i in j .. days-1){
                val t =getDaysAgo(i.toInt()).toString()
                val k =sp.getString(t, null)
                sp1.putString(t, k)
            }
            for(i in 1..j-1){
                val t =getDaysAgo(i.toInt()).toString()
                sp1.putString(t, "0")
            }
            val t =getDaysAgo(0)
            sp1.putString(t, time.toString())
            val p =sp.getString("last_day", null)
            sp.edit().clear().apply()
            sp1.putString("last_day",p).apply()
        }



        else{
            Base_create(days, context, time)
        }


    }








    fun getDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val cal = calendar.get(Calendar.DATE).toString()
        return cal
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun Date_Check(context: Context): Long{ // возвращает есть ли день
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)
        val mas = sp.getString("last_day", null)

        val d = LocalDate.now()
        val mas1 = LocalDate.parse(mas)
        val days = ChronoUnit.DAYS.between(d, mas1 )


        return days

}}