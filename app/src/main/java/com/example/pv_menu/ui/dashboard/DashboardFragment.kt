package com.example.pv_menu.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
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
import com.example.pv_menu.*
import com.example.pv_menu.databinding.FragmentDashboardBinding
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val idJs = sharedPreferences.getString("idJs", null)
        val repository = DashboardRepository(ApiClient.apiService)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        val webView = binding.webviewChart
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        if (idJs != null) {
            val systemId = when (idJs) {
                "huawei" -> "10"
                "id04" -> "11"
                "solaris" -> "12"
                else ->  Random.nextInt(13, 24).toString()
            }

            if (systemId != null) {
                val start = "2024-05-29T5:00"
                val end = "2024-05-29T21:30"

                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        viewModel.fetchPowerDataForSystem(systemId, start, end)
                    } catch (e: Exception) {
                        Log.e("DashboardFragment", "Failed to fetch power data", e)
                    }
                }
            } else {
                Log.e("DashboardFragment", "Unknown idJs value: $idJs")
            }
        } else {
            Log.e("DashboardFragment", "idJs is null")
        }

        viewModel.powerData.observe(viewLifecycleOwner, { data ->
            data.forEach { (systemId, generationPowers) ->
                Log.d("DashboardFragment", "System $systemId data received: $generationPowers")
            }
            val htmlContent = buildChartHtml(data)
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item1 -> {
                navigateToProprietatiSistem()
                true
            }

            R.id.menu_item2 -> {
                navigateToEvents()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToProprietatiSistem() {
        val intent = Intent(requireContext(), ProprietatiSistem::class.java)
        val idJs = sharedPreferences.getString("idJs", null)
        intent.putExtra("idJs", idJs)
        startActivity(intent)
    }

    private fun navigateToEvents() {
        val intent = Intent(requireContext(), Events::class.java)
        val idJs = sharedPreferences.getString("idJs", null)
        intent.putExtra("idJs", idJs)
        startActivity(intent)
    }

    private fun buildChartHtml(data: Map<String, List<GenerationPower>>): String {
        val chartScripts = data.map { (systemId, powerData) ->
            val labels = powerData.map { power ->
                val timestamp = power.timestamp
                if (timestamp != null && timestamp.isNotEmpty()) {
                    try {
                        // Parsează timestamp-ul folosind SimpleDateFormat cu formatul corect
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                        val date = sdf.parse(timestamp)

                        // Extrage ora și minutul din timestamp
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)
                        "$hour:$minute"
                    } catch (e: ParseException) {
                        Log.e("DashboardFragment", "Error parsing timestamp", e)
                        "N/A"
                    }
                } else {
                    "N/A"
                }
            }
            val values = powerData.map { it.generationMW }
            val labelsJson = labels.joinToString(prefix = "[\"", postfix = "\"]", separator = "\",\"")
            val valuesJson = values.joinToString(prefix = "[", postfix = "]", separator = ",")

            """
        <script>
            var ctx_$systemId = document.getElementById('chart_$systemId').getContext('2d');
            new Chart(ctx_$systemId, {
                type: 'line',
                data: {
                    labels: $labelsJson,
                    datasets: [{
                        label: 'Power Data for System $systemId',
                        data: $valuesJson,
                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                        borderColor: 'rgba(255, 99, 132, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        </script>
        """.trimIndent()
        }.joinToString("\n")

        val canvasElements = data.keys.joinToString("\n") { systemId ->
            "<canvas id='chart_$systemId' width='400' height='400'></canvas>"
        }

        return """
        <html>
        <head>
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        </head>
        <body>
            $canvasElements
            $chartScripts
        </body>
        </html>
    """.trimIndent()
    }


}
