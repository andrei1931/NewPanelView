package com.example.pv_menu.ui.dashboard
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pv_menu.ApiClient
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.GenerationPower
import com.example.pv_menu.ViewModelFactory
import com.example.pv_menu.databinding.FragmentDashboardBinding
import com.example.pv_menu.ui.dashboard.DashboardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiClient.apiService
        val repository = DashboardRepository(apiService)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        val start = "2024-04-02T10:58:00"
        val end = "2024-04-03T13:30:00"
        Log.d("DashboardFragment", "Fetching power data from $start to $end")

        // Log pentru a verifica încărcarea fișierului HTML în WebView
        loadWebView()

        GlobalScope.launch(Dispatchers.IO) {
            viewModel.fetchPowerData(start, end)
        }

        viewModel.powerData.observe(viewLifecycleOwner, Observer { data ->
            Log.d("DashboardFragment", "Power data fetched: $data")
            updateChart(data)
        })
    }

    private fun loadWebView() {
        val webView = binding.webviewChart
        webView.settings.javaScriptEnabled = true
        val urlToLoad = "file:///android_asset/chart.html"
        Log.d("DashboardFragment", "Loading URL: $urlToLoad")
        webView.loadUrl(urlToLoad)
    }

    private fun updateChart(data: List<GenerationPower>?) {
        data?.let {
            val jsonData: String = Gson().toJson(data)
            val webView: WebView = binding.webviewChart
            webView.settings.javaScriptEnabled = true

            webView.evaluateJavascript("updateChart('$jsonData');") { result ->
                Log.d("Dashboard Fragment", "Updated chart result: $result")
            }
        }

    }
}
