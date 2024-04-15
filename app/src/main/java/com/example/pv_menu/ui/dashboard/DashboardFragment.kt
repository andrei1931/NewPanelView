package com.example.pv_menu.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pv_menu.ApiClient
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.R
import com.example.pv_menu.ViewModelFactory
import com.example.pv_menu.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Inițializarea unui obiect de tip ApiService
        val apiService = ApiClient.apiService

        // Crearea unui obiect de tip DashboardRepository folosind constructorul corespunzător
        val repository = DashboardRepository(apiService)

        // Crearea unui obiect de tip ViewModelFactory și furnizarea repository-ului creat
        val viewModelFactory = ViewModelFactory(repository)

        // Obținerea ViewModel-ului utilizând ViewModelFactory-ul creat
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        // Specificarea intervalului de timp pentru cererea de date
        val start = "2024-04-02T10:58:00"
        val end = "2024-04-03T13:30:00"
        Log.d("DashboardFragment", "Fetching power data from $start to $end")

        // Pornirea cererii de date într-un fir de execuție separat
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.fetchPowerData(start, end)
        }

        // Observarea datelor de putere primite și actualizarea interfeței utilizatorului
        viewModel.powerData.observe(viewLifecycleOwner, Observer { data ->
            binding.textDashboard.text = data.toString()
            Log.d("DashboardFragment", "Power data fetched: $data")
        })
    }
}
