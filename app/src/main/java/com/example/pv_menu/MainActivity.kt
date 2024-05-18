package com.example.pv_menu

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pv_menu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main) // Inițializezi navController aici

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_harta,
                R.id.navigation_add
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Verificăm dacă suntem în fragmentul Dashboard
                if (navController.currentDestination?.id == R.id.navigation_dashboard) {
                    // Navigăm înapoi către fragmentul Home
                    navController.navigate(R.id.navigation_home)
                    Log.d("MainActivity", "Navigated back to Home from Dashboard")
                    true
                } else {
                    Log.d("MainActivity", "Not in Dashboard fragment, proceeding with default action")
                    super.onOptionsItemSelected(item)
                }
            }
            else -> {
                Log.d("MainActivity", "Selected item is not Home, proceeding with default action")
                super.onOptionsItemSelected(item)
            }
        }
    }

}
