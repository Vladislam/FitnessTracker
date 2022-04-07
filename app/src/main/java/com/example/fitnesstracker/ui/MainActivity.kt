package com.example.fitnesstracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.RunNavigationDirections
import com.example.fitnesstracker.databinding.ActivityMainBinding
import com.example.fitnesstracker.ui.fragments.TrackingFragmentDirections
import com.example.fitnesstracker.ui.viewmodels.MainViewModel
import com.example.fitnesstracker.util.const.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

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
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment -> bottomNavigationView.visibility =
                        View.VISIBLE
                    else -> bottomNavigationView.visibility = View.GONE
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
        return if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT && navController.currentDestination?.id == R.id.trackingFragment) {
            navController.navigate(
                TrackingFragmentDirections.actionTrackingFragmentToRunFragment(
                    viewModel.getPreferences().name
                )
            )
            true
        } else {
            navController.navigateUp() || super.onSupportNavigateUp()
        }
    }
}