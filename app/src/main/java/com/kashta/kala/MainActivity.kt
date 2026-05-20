package com.kashta.kala

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kashta.kala.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navView.setupWithNavController(navController)

        // Hide bottom nav on auth / admin / onboarding screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideOn = setOf(
                R.id.navigation_login,
                R.id.navigation_register,
                R.id.navigation_onboarding,
                R.id.navigation_admin,
                R.id.navigation_add_product,
                R.id.navigation_product_detail
            )
            binding.navView.visibility =
                if (destination.id in hideOn) View.GONE else View.VISIBLE
        }
    }
}
