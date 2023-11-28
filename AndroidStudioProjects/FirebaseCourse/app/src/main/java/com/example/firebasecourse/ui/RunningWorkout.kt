package com.example.firebasecourse.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebasecourse.R
import com.example.firebasecourse.db.RunDAO
import com.example.firebasecourse.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RunningWorkout : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_workout)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(findViewById(R.id.toolbar))

        val navView:com.google.android.material.bottomnavigation.BottomNavigationView=findViewById(R.id.bottomNavigationView)
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.navHostFragment)

        navView.setupWithNavController(fragment!!.findNavController())

        fragment.findNavController().addOnDestinationChangedListener{_,destination,_->

            when(destination.id){
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment->
                    navView.visibility= View.VISIBLE
                else->navView.visibility=View.GONE
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent:Intent?){
        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentById(R.id.navHostFragment)

            fragment?.findNavController()?.navigate(R.id.action_global_trackingFragment)
        }

    }

}