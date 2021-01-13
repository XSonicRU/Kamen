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
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.IDataSet


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
        Make_chart(4,5,requireContext())
        return root
    }



    fun Make_chart(n: Int, days: Int,context: Context){
        val sp = context.getSharedPreferences("settings_stats", Context.MODE_PRIVATE)
        //val qwe = arrayListOf<xAxis>()
        val entries = arrayListOf<BarEntry>()
        for (i in 0..days ) {
            val q = Data.getDaysAgo(i)
            val v =sp.getString(q,null)
             entries.add(BarEntry(5f, v!!.toFloat()))
        }
        //val entries1 = revenueComp1.mapIndexed { index, arrayList ->
        //    Entry(index, arrayList[index]) }
        val barDataSet = BarDataSet(entries, "Cells")
        barDataSet.setColor(Color.RED)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)
        val data = BarData(dataSets)
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        chart!!.setData(data);
        chart?.invalidate(); // refresh
    }



}