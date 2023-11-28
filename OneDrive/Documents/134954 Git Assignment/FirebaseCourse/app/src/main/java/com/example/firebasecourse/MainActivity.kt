package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.firebasecourse.databinding.ActivityMainBinding
import com.example.firebasecourse.ui.RunningWorkout

class MainActivity : AppCompatActivity() {

    //var btn: Button =findViewById(R.id.button3)
    lateinit var mainBinding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding= ActivityMainBinding.inflate(layoutInflater)
        val view=mainBinding.root
        setContentView(view)

        var btnWorkout: Button =findViewById(R.id.buttonWorkout)
        var btnProfile: Button =findViewById(R.id.buttonProfile)
        var btnFitBot: Button =findViewById(R.id.buttonFitBot)
        var btnTips: Button =findViewById(R.id.buttonTips)
        var btnNutrition: Button =findViewById(R.id.buttonNutrition)
        var btnSettings:Button=findViewById(R.id.buttonSettings)

        
        btnFitBot.setOnClickListener{
           startActivity(Intent(this, FitnessAssistant::class.java))

        }

        val userId=intent.getStringExtra("userIDP")

        btnProfile.setOnClickListener{

           val intent= Intent(this, UserProfileActivity::class.java)

            intent.putExtra("userID1",userId)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnWorkout.setOnClickListener {
            startActivity(Intent(this, RunningWorkoutContents::class.java))
        }

        btnNutrition.setOnClickListener {
            startActivity(Intent(this, WorkoutFeedbackHistory::class.java))
        }
    }
}