package com.dvpro.kamen

import android.content.SharedPreferences
import java.util.*

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.github.mikephil.charting.data.BarEntry
import java.util.*

object Data {
    // Переменная для трекинга отслеживания, если -1, то отслеживание не запущено,
    // если не -1, то тут время начала, инициализируется при запуске
    var TrackingStatus: Long = -1

    //Номер отслеживаемой маски, считаем с 1
    var TrackingIndex: Int = 1

    //SharedPreferences для получения настроек, инициализируется при запуске
    var prefman : SharedPreferences? = null

    //Лимит ношения маски, в секундах, инициализируется при запуске
    fun getMaskWearLimit(): Int{
        return prefman?.getString("limit","120")!!.toInt()*60
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

    fun Base_create(days:Int,last_day_val:Int =0,context: Context){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE).edit()
        val key_date = arrayOfNulls<String>(days)
        sp.putString(key_date[days-1],last_day_val.toString())
        key_date[key_date.size-1]=getDaysAgo(0).toString()
        for (i in 2..days){
            key_date[days-i]= getDaysAgo(i-1).toString()
            sp.putString(key_date[days-i],"0")
        }
        sp.putStringSet("key_date", key_date.toMutableSet() ).apply()
    }

    fun Statistic_update(time: Int,context: Context){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)
        val sp1 = sp.edit()
        val mas = sp.getStringSet("key_date", null)!!.toTypedArray()
        val calendar = Calendar.getInstance()
        val j =Date_Check(calendar.time.toString(),context)
        if(j==0){
            val today = sp.getString(mas.elementAt(mas.size-1), null)
            for(i in 0 .. mas.size-1){
                val k = sp.getString(mas.elementAt(i), null)
                sp1.putString(mas.elementAt(i),k)
            }
            sp1.putString(mas.elementAt(mas.size-1),(today!!.toInt() +time).toString() )
        }
        else if(j > 0){
            for (i in 0 .. mas.size-j-1){ //перепроверь правильно ли вычитаешь!!!!!!!
                val k = sp.getString(mas.elementAt(i), null)
                sp1.putString(mas.elementAt(0),k)
            }
            sp1.putString(mas.elementAt(mas.size-1),time.toString())
            mas[mas.size-1]=getDaysAgo(0).toString()
            for (i in 2..mas.size-j){
                mas[mas.size-i]= getDaysAgo(i-1).toString()
                sp1.putString(mas[mas.size-i],"0")
            }

            sp1.putStringSet("key_date", mas.toMutableSet() ).apply()
        }
        else{
            Base_create(mas.size,time,context)
        }


    }



    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.time
    }


    fun Date_Check(day:String,context: Context): Int{ // возвращает есть ли день
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)
        val mas = sp.getStringSet("key_date", null)
        for(i in mas!!.size-1..0){
            if(mas.elementAt(i)==day){
                return i
            }
        }
        return -1
    }



}