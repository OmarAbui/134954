package com.example.firebasecourse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasecourse.databinding.ActivityWorkoutFeedbackHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.RecyclerView

class WorkoutFeedbackHistory : AppCompatActivity() {

    lateinit var historyBinding: ActivityWorkoutFeedbackHistoryBinding

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    //The code below reaches the workout feedback table in the database
    val reference: DatabaseReference =database.reference.child("WorkoutFeedbacks")
    val feedbackList=ArrayList<WorkoutFeedbacks>()
    lateinit var feedbackAdapter:FeedbackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyBinding= ActivityWorkoutFeedbackHistoryBinding.inflate(layoutInflater)
        setContentView(historyBinding.root)

        retrieveWorkoutFeedback()


    }

    fun retrieveWorkoutFeedback(){

        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(eachFeedback in snapshot.children){

                    val feedback=eachFeedback.getValue(WorkoutFeedbacks::class.java)
                    if(feedback!=null){
                        println("wid:${feedback.wid}")
                        println("fDate:${feedback.fdate}")
                        println("fTime:${feedback.ftime}")

                        feedbackList.add(feedback)
                    }

                    feedbackAdapter= FeedbackAdapter(this@WorkoutFeedbackHistory, feedbackList)
                    historyBinding.Feedbacks.layoutManager=LinearLayoutManager(this@WorkoutFeedbackHistory)
                    historyBinding.Feedbacks.adapter=feedbackAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

}