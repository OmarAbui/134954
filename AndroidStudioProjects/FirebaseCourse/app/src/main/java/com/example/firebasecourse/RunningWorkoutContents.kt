package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.firebasecourse.ui.RunningWorkout

class RunningWorkoutContents : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running_workout_contents)

        val btnPerformRunning: Button =findViewById(R.id.buttonPerformRunning)
        val btnAddRunning:Button=findViewById(R.id.buttonAddRunning)

        btnPerformRunning.setOnClickListener {
            startActivity(Intent(this, RunningWorkout::class.java))
        }

        btnAddRunning.setOnClickListener {
            startActivity(Intent(this, AddRunningWorkouts::class.java))
        }
    }
}