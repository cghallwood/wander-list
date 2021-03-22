package com.colehallwood.wanderlist.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.colehallwood.wanderlist.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure map and list fragment to disable up-navigation
        val appBarConfiguration =
                AppBarConfiguration.Builder(R.id.listFragment, R.id.mapFragment).build()

        // Setting up app bar navigation with nav host
        setupActionBarWithNavController(findNavController(R.id.fr_main_nav), appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Sets up back-navigation in the nav host
        val navController = findNavController(R.id.fr_main_nav)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}