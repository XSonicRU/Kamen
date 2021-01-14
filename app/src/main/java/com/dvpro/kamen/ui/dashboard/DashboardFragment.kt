package com.dvpro.kamen.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dvpro.kamen.Data
import com.dvpro.kamen.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.Collections.swap


class DashboardFragment : Fragment() {
private var chart:  BarChart? =null
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
         chart =  root.findViewById<BarChart>(R.id.chart)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        Make_chart(4, 14, requireContext())
        return root
    }



    fun Make_chart(n: Int, days: Int, context: Context){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)
        val value = arrayListOf<String>()
        val key = arrayListOf<String>()
        val entries = arrayListOf<BarEntry>()
        for (i in 0 .. days){
            val q = Data.getDaysAgo(i)
            key.add(q)
            val v =sp.getString(q, null)
            value.add(v.toString())
        }
            quicksort(key,value,0,days.toLong())

        for (i in 0..days ) {
             entries.add(BarEntry(i.toFloat(), value[i]!!.toFloat() + 1000f))
        }
        val barDataSet = BarDataSet(entries, "Cells")
        barDataSet.setColor(Color.RED)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)
        val data = BarData(dataSets)
        data.setValueTextSize(10f);
        data.setBarWidth(0.5f);
        chart!!.setData(data);
        chart?.invalidate(); // refresh

    }

    fun quicksort(key:ArrayList<String>,value:ArrayList<String>,first:Long,last:Long){
        var f = first
        var l = last;
        val mid = value[((first+last)/2).toInt()].toLong(); //?????????? ???????? ????????
        do {
            while (value[f.toInt()].toInt() < mid) f++
            while (value[l.toInt()].toInt() > mid) l--;
            if (f <= l) //???????????? ?????????
            {
                val k = value[l.toInt()]
                value[l.toInt()] = value[f.toInt()]
                value[f.toInt()] =k
                val q=key[l.toInt()]
                key[l.toInt()]=key[f.toInt()]
                key[f.toInt()] =q
                f++;
                l--;
            }
        } while (f < l);
        if (first < l) quicksort(key,value, first, l);
        if (f < last) quicksort(key,value, f, last);
    }



}