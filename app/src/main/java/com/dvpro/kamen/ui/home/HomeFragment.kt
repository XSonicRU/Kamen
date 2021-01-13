package com.dvpro.kamen.ui.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.dvpro.kamen.Data
import com.dvpro.kamen.R
import java.util.*
import kotlin.concurrent.fixedRateTimer

class HomeFragment : Fragment() {
    private var isNotifySent: Boolean = false
    private var statusLabel: TextView? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button: Button = root.findViewById(R.id.button)
        statusLabel = root.findViewById(R.id.text_home)
        button.setOnClickListener {
            if (Data.TrackingStatus == -1L) {
                Data.curtimer = startTracking(button)
                Data.TrackingStatus = System.currentTimeMillis()
                Data.sp!!.edit().putLong("TrackingStatus", Data.TrackingStatus).apply()
            } else {
                Data.curtimer!!.cancel()
                button.text = getString(R.string.button_value_on)
                Data.Statistic_update((System.currentTimeMillis()-Data.getMaskWearLimit()/1000).toInt(),requireContext())
                Data.sp!!.edit().putLong("TrackingStatus", -1L).apply()
                NotificationManagerCompat.from(requireContext()).cancel(0)
                Data.TrackingStatus = -1L
                activity?.findViewById<TextView>(R.id.text_home)!!.visibility = View.INVISIBLE
            }
        }
        Data.prefman = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (Data.TrackingStatus != -1L) {
            Data.curtimer = startTracking(button)
        }
        return root
    }

    private fun startTracking(button: Button): Timer {
        button.text = getString(R.string.button_value_off)
        isNotifySent = false
        statusLabel!!.findViewById<TextView>(R.id.text_home)!!.visibility = View.VISIBLE
        return fixedRateTimer("timer", false, 0L, 1000) {
            val t: Long = (System.currentTimeMillis() - Data.TrackingStatus) / 1000
            val hrs = t / 3600
            val min = (t - hrs * 60) / 60
            val sec = t - min * 60
            val output: String;
            if (Locale.getDefault().displayLanguage == "русский") {
                output = getString(R.string.wearing_label_full, if (hrs > 0) " " + Data.choosePluralMerge(hrs, "час", "часа", "часов") else "",
                        if (min > 0) " " + Data.choosePluralMerge(min, "минуту", "минуты", "минут") else "",
                        " " + Data.choosePluralMerge(sec, "секунду", "секунды", "секунд"))
            } else {
                output = getString(R.string.wearing_label_full, if (hrs > 0) (if ((hrs.toString()[hrs.toString().length - 1] == '1') && (hrs != 11L)) " $hrs hour" else " $hrs hours") else "",
                        if (min > 0) (if ((min.toString()[min.toString().length - 1] == '1') && (min != 11L)) " $min minute" else " $min minutes") else "",
                        if ((sec.toString()[sec.toString().length - 1] == '1') && (sec != 11L)) " $sec second" else " $sec seconds")
            }
            val maskState = t < Data.getMaskWearLimit()
            var alertonce = true
            if(!maskState && !isNotifySent){
                alertonce = false
                isNotifySent = true
            }
            show_notification(output, if (maskState) getString(R.string.good_mask_state) else (getString(R.string.bad_mask_state)), requireContext(), true, 0,alertonce)
            activity?.runOnUiThread {
                statusLabel!!.setTextColor(if (maskState)Color.GREEN else Color.RED)
                statusLabel!!.text = output
            }
        }
    }

    fun show_notification(content: String?, title: String?, c: Context, perm: Boolean, id: Int, alertOnce:Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val builder = Notification.Builder(c)
                    .setSmallIcon(R.drawable.ic_baseline_masks_24)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOnlyAlertOnce(alertOnce)
                   // .addAction(action)
                    .setOngoing(perm)
            val nm = NotificationManagerCompat.from(c)
            nm.notify(id, builder.build())
        } else {
            val builder = Notification.Builder(c, "mask_def_channel")
                    .setSmallIcon(R.drawable.ic_baseline_masks_24)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOnlyAlertOnce(alertOnce)
                   // .addAction(action)
                    .setOngoing(perm)
            val nm = NotificationManagerCompat.from(c)
            nm.notify(id, builder.build())
        }
    }
}