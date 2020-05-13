package com.example.snapventuremultiplayer.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationButton: Button
    private lateinit var mode1vs1Button: Button
    private lateinit var mode2vs2Button: Button
    private lateinit var mode3vs3Button: Button
    private lateinit var createRoomButton: Button
    private lateinit var joinRoomButton: Button
    private lateinit var freeRoomButton: Button
    private lateinit var quickStartButton: Button
    private lateinit var PlayerName: TextView
    private lateinit var LevelPlayer: TextView
    private lateinit var TextLokasi: TextView
    private lateinit var Textmodee: TextView

    private fun setupViewBinding(view: View) {
        locationButton = view.findViewById(R.id.lokasiMain)
        mode1vs1Button = view.findViewById(R.id.btn1vs1)
        mode2vs2Button = view.findViewById(R.id.btn2vs2)
        mode3vs3Button = view.findViewById(R.id.btn3vs3)
        createRoomButton = view.findViewById(R.id.btncreateRoom)
        joinRoomButton = view.findViewById(R.id.btnjoinRoom)
        freeRoomButton = view.findViewById(R.id.btnfreeRoom)
        quickStartButton = view.findViewById(R.id.btnQuickStart)
        PlayerName = view.findViewById(R.id.namaUser)
        LevelPlayer = view.findViewById(R.id.levelUser)
        TextLokasi = view.findViewById(R.id.lokasi)
        Textmodee = view.findViewById(R.id.modeMain)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setupViewBinding(root)
        return root
    }



}