package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivitySettingsBinding
import com.example.firebasecourse.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    val auth:FirebaseAuth=FirebaseAuth.getInstance()
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference.child("Users")
    lateinit var settingsBinding: ActivitySettingsBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding= ActivitySettingsBinding.inflate(layoutInflater)
        val view=settingsBinding.root
        setContentView(view)


        val btnDelete: Button =findViewById(R.id.buttonDelete)

        btnDelete.setOnClickListener {
            reference.child(auth.currentUser!!.uid).removeValue().addOnCompleteListener {task->
                if(task.isSuccessful){
                    auth.currentUser?.delete()?.addOnCompleteListener { taskTwo->
                        if(taskTwo.isSuccessful){
                            Toast.makeText(applicationContext, "Account Completely Deleted!!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Login::class.java))
                        }
                    }
                }
            }
        }
    }
}