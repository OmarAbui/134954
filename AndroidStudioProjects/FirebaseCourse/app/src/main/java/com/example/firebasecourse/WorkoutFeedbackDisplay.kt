package com.example.firebasecourse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar

class WorkoutFeedbackDisplay : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference =database.reference.child("WorkoutFeedbacks")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_feedback_display)

        val feedbackView:TextView=findViewById(R.id.textView10)
        val homeButton: Button =findViewById(R.id.button5)

        val response = intent.getStringExtra("response")
        if (response != null && response != "That didn't work!") {
            // Display the response if it's not null and not the error message
            feedbackView.text = response
        } else {
            feedbackView.text = "No valid response available"
        }

        val wId=reference.push().key.toString()
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val currentDate = dateFormat.format(currentDateTime)
        val currentTime = timeFormat.format(currentDateTime)

        val feedback=WorkoutFeedbacks(wId, auth.currentUser!!.uid,currentDate,currentTime,response.toString())

        reference.child(wId).setValue(feedback).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(this, "Feedback Saved To Database!!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "There was a problem when saving your feedback to the database!!", Toast.LENGTH_LONG).show()
            }

        }

        homeButton.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))

        }

    }



}