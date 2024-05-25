package com.example.pv_menu.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pv_menu.ApiClient
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.Events
import com.example.pv_menu.GenerationPower
import com.example.pv_menu.Inregistrare
import com.example.pv_menu.MainActivity
import com.example.pv_menu.ProprietatiSistem
import com.example.pv_menu.R
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        sharedPrefsUtil = SharedPrefsUtil(requireContext()) // Inițializare clasa utilitară pentru SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Inițializare ViewModel
        val repository = DashboardRepository(ApiClient.apiService)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        // Inițializare WebView
        val webView = binding.webviewChart
        val webSettings: WebSettings = webView.settings

        webSettings.javaScriptEnabled = true // Activate JavaScript support

        // Attempt to load data from SharedPreferences
        val cachedData = sharedPrefsUtil.getData("power_data")
        if (cachedData != null) {
            val powerData = Gson().fromJson<List<GenerationPower>>(
                cachedData,
                object : TypeToken<List<GenerationPower>>() {}.type
            )
            viewModel.setCachedPowerData(powerData)
        } else {
            // If there is no cached data, make a request to the server
            val start = "2024-04-29T12:58:00"
            val end = "2024-04-30T13:30:00"
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.fetchPowerData(start, end)
            }
        }

        // Observe received power data and build and display the chart in WebView
        viewModel.powerData.observe(viewLifecycleOwner, { data ->
            val id = arguments?.getString("id") ?: sharedPreferences.getString("idJs", null) // Fall back to saved value
            val locatie = arguments?.getString("locatie") ?: sharedPreferences.getString("locatieJs", null) // Fall back to saved value
            val htmlContent = buildChartHtml(data, id, locatie)
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

            // Save data in SharedPreferences
            val jsonPowerData = Gson().toJson(data)
            sharedPrefsUtil.saveData("power_data", jsonPowerData)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item1-> {
                val intent = Intent(requireContext(), ProprietatiSistem::class.java)
                val idJs = sharedPreferences.getString("idJs", null)
                intent.putExtra("idJs", idJs)
                startActivity(intent)
                true
            }
            R.id.menu_item2 -> {
                // Gestionează acțiunea pentru al doilea element de meniu
                val intent = Intent(requireContext(), Events::class.java)
                val idJs = sharedPreferences.getString("idJs", null)
                intent.putExtra("idJs", idJs)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun buildChartHtml(data: List<GenerationPower>, id: String?, locatie: String?): String {
        // Construct the HTML content for displaying the chart
        val labels = (0 until data.size).map { "Entry $it" }
        val values = data.map { it.generationMW }
        val labelsJson = labels.joinToString(prefix = "[\"", postfix = "\"]", separator = "\",\"")
        val valuesJson = values.joinToString(prefix = "[", postfix = "]", separator = ",")
        val idJs = id ?: ""
        val locatieJs = locatie ?: ""
        return """
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <canvas id="myChart" width="400" height="400"></canvas>
                <p>ID: $idJs</p>
                <p>Locație: $locatieJs</p>
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
