package com.example.firebasecourse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import org.json.JSONObject


class WorkoutFeedback : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference =database.reference.child("Users")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var answer:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_feedback)

        val distance:EditText=findViewById(R.id.editTextDistance)
        val calories:EditText=findViewById(R.id.editTextCalories)
        val speed:EditText=findViewById(R.id.editTextSpeed)
        val weight:EditText=findViewById(R.id.editTextWeight)
        val height:EditText=findViewById(R.id.editTextHeight)
        val btnFeedback: Button =findViewById(R.id.buttonGetFeedback)

        reference.child(auth.currentUser!!.uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {

                    val userHeight = snapshot.child("height").getValue(Long::class.java)
                    val userWeight = snapshot.child("weight").getValue(Long::class.java)

                    height.text= Editable.Factory.getInstance().newEditable(userHeight.toString())
                    weight.text=Editable.Factory.getInstance().newEditable(userWeight.toString())


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btnFeedback.setOnClickListener {
            val distance=distance.text.toString()
            val speed=speed.text.toString()
            val caloriesBurned=calories.text.toString()
            val weight= weight.text.toString()
            val height=height.text.toString()

            var question="Hello, today i ran $distance km at a speed of $speed km/hour. I managed to burn" +
                    "$caloriesBurned kcal. My weight is $weight kg and my height is $height cm. Please " +
                    "give me a brief analysis of my workout in 3 brief sentences that are in a point-format and a brief conclusion on what to improve on that a user will not get tired reading on. Make it in a structured format. You " +
                    "can structure everything in a point format, for example, the km a user has covered in its own line, the user's weight" +
                    "in its own line, also, if the conclusion can also be in a point format, that would be much better?"

            sendQuestion(question)
            Snackbar.make(it, "Just a moment, we are processing your feedback", Snackbar.LENGTH_LONG).show()



        }



    }


    fun sendQuestion(input:String){
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openai.com/v1/completions"

        val jsonObject=JSONObject()
        jsonObject.put("prompt", input)
        jsonObject.put("model","gpt-3.5-turbo-instruct-0914")
        jsonObject.put("max_tokens", 1500)
        jsonObject.put("temperature",0)

        // Requesting a string response from the provided URL.
        val stringRequest = object:JsonObjectRequest(
            Request.Method.POST, url,jsonObject,
            Response.Listener<JSONObject> { response ->

                // Display the first 500 characters of the response string.
                var result=response.getJSONArray("choices").getJSONObject(0).getString("text")
                answer = result

                val intent = Intent(this, WorkoutFeedbackDisplay::class.java)
                intent.putExtra("response", answer)
                startActivity(intent)

            },
            Response.ErrorListener { answer = "That didn't work!" })
        {
            override fun getHeaders(): MutableMap<String, String> {

                var map=HashMap<String, String>()
                map.put("Content-Type", "application/json")
                map.put("Authorization", "Bearer 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")
                return map
            }
        }

        stringRequest.setRetryPolicy(object:RetryPolicy{
            override fun getCurrentTimeout(): Int {
                return 60000
            }

            override fun getCurrentRetryCount(): Int {
                return 6
            }

            override fun retry(error: VolleyError?) {

            }

        })

// Add the request to the RequestQueue.
        queue.add(stringRequest)



    }


}