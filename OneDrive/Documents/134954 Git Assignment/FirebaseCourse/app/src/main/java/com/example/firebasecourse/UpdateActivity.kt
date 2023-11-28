package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityUpdateBinding
import com.example.firebasecourse.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateActivity : AppCompatActivity() {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference.child("Users")
    lateinit var updateBinding:ActivityUpdateBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateBinding= ActivityUpdateBinding.inflate(layoutInflater)
        val view=updateBinding.root
        setContentView(view)

        val nameView:EditText=findViewById(R.id.editTextName)
        val ageView:EditText=findViewById(R.id.editTextAge)
        val heightView:EditText=findViewById(R.id.editTextHeight)
        val weightView:EditText=findViewById(R.id.editTextWeight)
        val emailView:EditText=findViewById(R.id.editTextEmail)
        val phoneView:EditText=findViewById(R.id.editTextPhone)
        val update: Button =findViewById(R.id.buttonUpdate)

            val name=intent.getStringExtra("name")
            val age=intent.getIntExtra("age",0)
            val height=intent.getIntExtra("height",0)
            val weight=intent.getIntExtra("weight",0)
            val phone=intent.getStringExtra("phone")
            val email=intent.getStringExtra("email")
           // val userId=intent.getStringExtra("userID2")


        nameView.setText(name)
        ageView.setText(age.toString())
        heightView.setText(height.toString())
        weightView.setText(weight.toString())
        emailView.setText(email)
        phoneView.setText(phone)

        update.setOnClickListener{
            val nameUpdated=nameView.text.toString()
            val ageUpdated=ageView.text.toString().toInt()
            val heightUpdated=heightView.text.toString().toInt()
            val weightUpdated=weightView.text.toString().toInt()
            val emailUpdated=emailView.text.toString()
            val phoneUpdated=phoneView.text.toString()


            val userMap= mutableMapOf<String, Any>()
                userMap["name"]= nameUpdated
                userMap["age"]=ageUpdated
                userMap["height"]=heightUpdated
                userMap["weight"]=weightUpdated
                userMap["email"]=emailUpdated
                userMap["phone"]=phoneUpdated

            //val userId=intent.getStringExtra("userID2")
            reference.child(auth.currentUser!!.uid).updateChildren(userMap).addOnCompleteListener {task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"You have successfully updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,UserProfileActivity::class.java))
                }else{
                    Toast.makeText(applicationContext,"Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }



        }






    }
}
