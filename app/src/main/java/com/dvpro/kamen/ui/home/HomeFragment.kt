package com.dvpro.kamen.ui.home

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dvpro.kamen.Data
import com.dvpro.kamen.R
import java.util.*
import kotlin.concurrent.fixedRateTimer

class HomeFragment : Fragment() {

    private var statusLabel: TextView? = null
    private var curtimer: Timer? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val sp = requireContext().getSharedPreferences("settings", MODE_PRIVATE)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button: Button = root.findViewById(R.id.button)
        statusLabel = root.findViewById(R.id.text_home)
        button.setOnClickListener {
            if (Data.TrackingStatus == -1L) {
                curtimer = startTracking(button)
                Data.TrackingStatus = System.currentTimeMillis()
                sp.edit().putLong("TrackingStatus", Data.TrackingStatus).apply()
            } else {
                curtimer!!.cancel()
                button.text = getString(R.string.button_value_on)
                sp.edit().putLong("TrackingStatus", -1L).apply()
                //TODO: вызов сохранения в стату
                Data.TrackingStatus = -1L
                activity?.findViewById<TextView>(R.id.text_home)!!.visibility = View.INVISIBLE
            }
        }

        Data.TrackingStatus = sp.getLong("TrackingStatus", -1L)
        if(Data.TrackingStatus!=-1L){
            curtimer = startTracking(button)
        }
        return root
    }
    private fun startTracking(button:Button) : Timer{
        button.text = getString(R.string.button_value_off)
        statusLabel!!.findViewById<TextView>(R.id.text_home)!!.visibility = View.VISIBLE
        return fixedRateTimer("timer", false, 0L, 1000) {
            activity?.runOnUiThread {
                if (Data.TrackingStatus != -1L) {
                    val t: Long = (System.currentTimeMillis() - Data.TrackingStatus) / 1000
                    val hrs = t / 3600
                    val min = (t - hrs * 60) / 60
                    val sec = t - min * 60
                    if (Locale.getDefault().displayLanguage == "русский") {
                        statusLabel!!.text = getString(R.string.wearing_label_full, if (hrs > 0) " " + Data.choosePluralMerge(hrs, "час", "часа", "часов") else "",
                                if (min > 0) " " + Data.choosePluralMerge(min, "минуту", "минуты", "минут") else "",
                                " " + Data.choosePluralMerge(sec, "секунду", "секунды", "секунд"))
                    } else {
                        statusLabel!!.text = getString(R.string.wearing_label_full, if (hrs > 0) (if ((hrs.toString()[hrs.toString().length - 1] == '1') && (hrs != 11L)) " $hrs hour" else " $hrs hours") else "",
                                if (min > 0) (if ((min.toString()[min.toString().length - 1] == '1') && (min != 11L)) " $min minute" else " $min minutes") else "",
                                if ((sec.toString()[sec.toString().length - 1] == '1') && (sec != 11L)) " $sec second" else " $sec seconds")
                    }
                }
            }
        }
    }
}