package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityPhoneBinding
import com.example.firebasecourse.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.TimeUnit

class UserProfileActivity : AppCompatActivity() {

    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference.child("Users")
    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var profileBinding:ActivityUserProfileBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding= ActivityUserProfileBinding.inflate(layoutInflater)
        val view=profileBinding.root
        setContentView(view)

        val name:TextView=findViewById(R.id.tvName)
        val age:TextView=findViewById(R.id.tvAge)
        val height:TextView=findViewById(R.id.tvHeight)
        val weight:TextView=findViewById(R.id.tvWeight)
        val phone:TextView=findViewById(R.id.tvPhone)
        val email:TextView=findViewById(R.id.tvEmail)
        val welcome:TextView=findViewById(R.id.tvWelcome)
        val edit: Button =findViewById(R.id.buttonEdit)




        //val userId=intent.getStringExtra("userID1")



         reference.child(auth.currentUser!!.uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {

                        val userName = snapshot.child("name").getValue<String>()
                        val userAge = snapshot.child("age").getValue(Long::class.java)
                        val userHeight = snapshot.child("height").getValue(Long::class.java)
                        val userWeight = snapshot.child("weight").getValue(Long::class.java)
                        val userPhone = snapshot.child("phone").getValue(String::class.java)
                        val userEmail = snapshot.child("email").getValue<String>()
                        val userWelcome = snapshot.child("name").getValue<String>()

                        welcome.text = "Welcome " + userWelcome
                        name.text =userName

                        height.text = userHeight.toString()
                        weight.text = userWeight.toString()
                        phone.text = userPhone
                        email.text =userEmail
                        age.text = userAge.toString()
                    }
                }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        edit.setOnClickListener{
            val details= Intent(this,UpdateActivity::class.java)
            details.putExtra("name",name.text)
            details.putExtra("height",height.text.toString().toInt())
            details.putExtra("weight",weight.text.toString().toInt())
            details.putExtra("age",age.text.toString().toInt())
            details.putExtra("email",email.text)
            details.putExtra("phone",phone.text)
            //details.putExtra("userID2",userId)
            startActivity(details)

        }

    }

}