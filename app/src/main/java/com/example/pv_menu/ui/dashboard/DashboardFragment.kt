package com.example.pv_menu.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pv_menu.ApiClient
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.GenerationPower
import com.example.pv_menu.SharedPrefsUtil
import com.example.pv_menu.ViewModelFactory
import com.example.pv_menu.databinding.FragmentDashboardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var sharedPrefsUtil: SharedPrefsUtil // Clasa utilitară pentru gestionarea SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        sharedPrefsUtil = SharedPrefsUtil(requireContext()) // Inițializare clasa utilitară pentru SharedPreferences
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inițializare ViewModel
        val repository = DashboardRepository(ApiClient.apiService)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        // Inițializare WebView
        val webView = binding.webviewChart
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // Activează suportul pentru JavaScript

        // Încercare de încărcare a datelor din SharedPreferences
        val cachedData = sharedPrefsUtil.getData("power_data")
        if (cachedData != null) {
            val powerData = Gson().fromJson<List<GenerationPower>>(
                cachedData,
                object : TypeToken<List<GenerationPower>>() {}.type
            )

            viewModel.setCachedPowerData(powerData)
        } else {
            // Dacă nu există date salvate în cache, facem cererea către server
            val start = "2024-04-29T12:58:00"
            val end = "2024-04-30T13:30:00"
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.fetchPowerData(start, end)
            }
        }

        // Observarea datelor de putere primite și construirea și afișarea graficului în WebView
        viewModel.powerData.observe(viewLifecycleOwner, { data ->
            val htmlContent = buildChartHtml(data)
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

            // Salvare date în SharedPreferences
            val jsonPowerData = Gson().toJson(data)
            sharedPrefsUtil.saveData("power_data", jsonPowerData)
        })
    }

    private fun buildChartHtml(data: List<GenerationPower>): String {
        // Construiește conținutul HTML pentru afișarea graficului
        val labels = (0 until data.size).map { "Entry $it" }
        val values = data.map { it.generationMW }
        val labelsJson = labels.joinToString(prefix = "[\"", postfix = "\"]", separator = "\",\"")
        val valuesJson = values.joinToString(prefix = "[", postfix = "]", separator = ",")
        return """
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <canvas id="myChart" width="400" height="400"></canvas>
                <script>
                    var ctx = document.getElementById('myChart').getContext('2d');
                    var myChart = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: $labelsJson,
                            datasets: [{
                                label: 'Power Data',
                                data: $valuesJson,
                                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                borderColor: 'rgba(255, 99, 132, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            scales: {
                                yAxes: [{
                                    ticks: {
                                        beginAtZero: true
                                    }
                                }]
                            }
                        }
                    });
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}
