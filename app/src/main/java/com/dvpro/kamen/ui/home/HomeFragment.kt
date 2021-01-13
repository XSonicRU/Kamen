package com.dvpro.kamen.ui.home

import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.dvpro.kamen.Data
import com.dvpro.kamen.R
import java.util.*
import kotlin.concurrent.fixedRateTimer


class HomeFragment : Fragment() {
    private var isNotifySent: Boolean = false
    private var statusLabel: TextView? = null
    private var adviceLabel: TextView? = null
    private var spinner: Spinner? = null
    private var resetButton: Button? = null

    //private var progressBar: ProgressBar? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button: Button = root.findViewById(R.id.button)
        resetButton = root.findViewById(R.id.button2)
        statusLabel = root.findViewById(R.id.text_home)
        adviceLabel = root.findViewById(R.id.text_home2)
        spinner = root.findViewById<Spinner>(R.id.spinner2)
        Data.prefman = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val maskAmount = Data.prefman!!.getString("mask_amount", "1")!!.toInt()
        val spinnerArray: MutableList<String> = ArrayList()
        for (i in 1..maskAmount) {
            spinnerArray.add(getString(R.string.mask_label) + " $i")
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (Data.launchAquisition) {
                    Data.curTracking = spinner!!.selectedItemPosition
                    Data.sp!!.edit().putInt("LastMask", Data.curTracking).apply()
                } else {
                    spinner!!.setSelection(Data.curTracking)
                    Data.launchAquisition = true
                }
                onNewMaskSelection()
            }
        }
        //progressBar = root.findViewById(R.id.progressBar)
        button.setOnClickListener {
            if (Data.TrackingStatus == -1L) {
                Data.curtimer = startTracking(button)
                Data.TrackingStatus = System.currentTimeMillis()
                Data.sp!!.edit().putLong("TrackingStatus", Data.TrackingStatus).apply()
                adviceLabel!!.visibility = View.VISIBLE
                spinner!!.visibility = View.INVISIBLE
                resetButton!!.visibility = View.INVISIBLE
            } else {
                Data.curtimer!!.cancel()
                button.text = getString(R.string.button_value_on)
                Data.Statistic_update(((System.currentTimeMillis() - Data.TrackingStatus) / 1000).toInt(), requireContext())
                val editor = Data.sp!!.edit()
                Data.CurMaskWear += ((System.currentTimeMillis() - Data.TrackingStatus) / 1000)
                editor.putLong("Mask " + spinner!!.selectedItemPosition, Data.CurMaskWear)
                editor.putLong("TrackingStatus", -1L).apply()
                NotificationManagerCompat.from(requireContext()).cancel(0)
                Data.TrackingStatus = -1L
                onNewMaskSelection()
                spinner!!.visibility = View.VISIBLE
                resetButton!!.visibility = View.VISIBLE
            }
        }
        resetButton!!.setOnClickListener {
            Data.sp!!.edit().putLong("Mask " + spinner!!.selectedItemPosition, 0).apply()
            onNewMaskSelection()
        }
        if (Data.TrackingStatus != -1L) {
            Data.curtimer = startTracking(button)
        }
        return root
    }

    private fun getGeneralOutput(): String {
        if(Data.CurMaskWear != 0L){
            val hrs = Data.CurMaskWear / 3600
            val min = (Data.CurMaskWear - hrs * 3600) / 60
            val sec = (Data.CurMaskWear - hrs * 3600) - min * 60
            val output: String
            if (Locale.getDefault().displayLanguage == "русский") {
                output = ((if (hrs > 0) " " + Data.choosePluralMerge(hrs, "час", "часа", "часов") else "") +
                        (if (min > 0) " " + Data.choosePluralMerge(min, "минута", "минуты", "минут") else "") +
                        (" " + Data.choosePluralMerge(sec, "секунда", "секунды", "секунд")))
            } else {
                output = ((if (hrs > 0) (if ((hrs.toString()[hrs.toString().length - 1] == '1') && (hrs != 11L)) " $hrs hour" else " $hrs hours") else "") +
                        (if (min > 0) (if ((min.toString()[min.toString().length - 1] == '1') && (min != 11L)) " $min minute" else " $min minutes") else "") +
                        (if ((sec.toString()[sec.toString().length - 1] == '1') && (sec != 11L)) " $sec second" else " $sec seconds"))
            }
            return output
        }else{
            return getString(R.string.mask_perfect)
        }
    }

    private fun onNewMaskSelection() {
        statusLabel!!.text = getString(R.string.wear_label)
        Data.CurMaskWear = Data.sp!!.getLong("Mask ${spinner!!.selectedItemPosition}", 0)
        val maskState = Data.CurMaskWear < Data.getMaskWearLimit()
        adviceLabel!!.setTextColor(if (maskState) Color.GREEN else Color.RED)
        statusLabel!!.setTextColor(if (maskState) Color.GREEN else Color.RED)
        adviceLabel!!.text = getGeneralOutput()
    }

    private fun startTracking(button: Button): Timer {
        button.text = getString(R.string.button_value_off)
        isNotifySent = false
        adviceLabel!!.visibility = View.VISIBLE
        spinner!!.visibility = View.INVISIBLE
        resetButton!!.visibility = View.INVISIBLE
        return fixedRateTimer("timer", false, 0L, 1000) {
            val t: Long = ((System.currentTimeMillis() - Data.TrackingStatus) / 1000) + Data.CurMaskWear
            val hrs = t / 3600
            val min = (t - hrs * 3600) / 60
            val sec = (t - hrs * 3600) - min * 60
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
            if (!maskState && !isNotifySent) {
                alertonce = false
                isNotifySent = true
            }
            show_notification(output, if (maskState) getString(R.string.good_mask_state) else (getString(R.string.bad_mask_state)), requireContext(), true, 0, alertonce)
            activity?.runOnUiThread {
                //progressBar!!.progress = 100-((Data.getMaskWearLimit()-t)/Data.getMaskWearLimit().toDouble()*100).toInt()
                adviceLabel!!.setTextColor(if (maskState) Color.GREEN else Color.RED)
                adviceLabel!!.text = if (maskState) getString(R.string.good_mask_state) else (getString(R.string.bad_mask_state))
                statusLabel!!.setTextColor(if (maskState) Color.GREEN else Color.RED)
                statusLabel!!.text = output
            }
        }
    }

    fun show_notification(content: String?, title: String?, c: Context, perm: Boolean, id: Int, alertOnce: Boolean) {
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