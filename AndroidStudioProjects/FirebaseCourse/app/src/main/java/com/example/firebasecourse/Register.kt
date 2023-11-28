package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityRegisterBinding
import com.example.firebasecourse.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class Register : AppCompatActivity() {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    //The code below reaches the main database
    val reference:DatabaseReference=database.reference
    lateinit var registerBinding: ActivityRegisterBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBinding= ActivityRegisterBinding.inflate(layoutInflater)
        val view=registerBinding.root
        setContentView(view)

        val regButton: Button =findViewById(R.id.button)
        val alreadyButton: Button =findViewById(R.id.button2)
        val userEmail:EditText=findViewById(R.id.editTextEmail)
        val userPassword:EditText=findViewById(R.id.editTextPassword)
        val userName:EditText=findViewById(R.id.editTextName)
        val userPhone:EditText=findViewById(R.id.editTextPhone)
        val userAge:EditText=findViewById(R.id.editTextAge)
        val userHeight:EditText=findViewById(R.id.editTextHeight)
        val userWeight:EditText=findViewById(R.id.editTextWeight)

        regButton.setOnClickListener{

            val uEmail=userEmail.text.toString()
            val uPassword=userPassword.text.toString()
            val uName=userName.text.toString()
            val uPhone=userPhone.text.toString()
            val uAge=userAge.text.toString().toInt()
            val uHeight=userHeight.text.toString().toInt()
            val uWeight=userWeight.text.toString().toInt()


            register(uEmail,uPassword,uName,uPhone,uAge,uHeight,uWeight)

        }

        alreadyButton.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }


    }
    fun register(email:String, password:String, name:String, phoneNumber: String, age:Int, height:Int, weight:Int){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            val userId:String=auth.currentUser!!.uid
            if(task.isSuccessful){
                val getRef=reference.child("Users").child(userId)
                val users=Users(email,userId,name,age,phoneNumber,height,weight)
                getRef.setValue(users).addOnCompleteListener {task->

                    if(task.isSuccessful)   {
                     Toast.makeText(applicationContext, "You have been added to the database!", Toast.LENGTH_SHORT).show()
                        val intent=Intent(this, Login::class.java)
                        startActivity(intent)

                 }else{
                     Toast.makeText(applicationContext, "Sorry, You have been not been added to the database!", Toast.LENGTH_SHORT).show()
                 }

                }

            }else{
                //Reason for the error
                Toast.makeText(applicationContext, task.exception?.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }
}