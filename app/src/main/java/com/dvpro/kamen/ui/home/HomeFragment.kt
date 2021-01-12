package com.dvpro.kamen.ui.home

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dvpro.kamen.Data
import com.dvpro.kamen.R
import java.time.Instant
import java.util.prefs.Preferences

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val button: Button = root.findViewById(R.id.button)
        button.setOnClickListener {
            if (Data.TrackingStatus == -1L) {
                button.text = resources.getText(R.string.button_value_on)
                Data.TrackingStatus = System.currentTimeMillis()
            } else {
                button.text = resources.getText(R.string.button_value_off)
            }
        }
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        val sp = requireContext().getSharedPreferences("settings", MODE_PRIVATE);
        sp.getInt("TrackingStatus", -1)

    }
}