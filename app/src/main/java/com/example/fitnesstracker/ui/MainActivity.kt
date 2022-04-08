package com.example.fitnesstracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.RunNavigationDirections
import com.example.fitnesstracker.databinding.ActivityMainBinding
import com.example.fitnesstracker.util.const.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun setupBottomNavigation() {
        binding.apply {
            navController =
                (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

            val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.runFragment,
                R.id.statisticsFragment,
                R.id.settingsFragment,
                R.id.setupFragment
            ).build()

            setupActionBarWithNavController(navController, appBarConfiguration)
            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.settingsFragment -> {
                        bottomNavigationView.visibility = View.VISIBLE
                        actionBar?.title = destination.label
                    }
                    R.id.runFragment -> {
                        bottomNavigationView.visibility = View.VISIBLE
                    }
                    R.id.statisticsFragment -> {
                        bottomNavigationView.visibility = View.VISIBLE
                        actionBar?.title = destination.label
                    }
                    else -> {
                        bottomNavigationView.visibility = View.GONE
                        actionBar?.title = destination.label
                    }
                }
            }
        }
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navController.navigate(RunNavigationDirections.actionGlobalTrackingFragment())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}