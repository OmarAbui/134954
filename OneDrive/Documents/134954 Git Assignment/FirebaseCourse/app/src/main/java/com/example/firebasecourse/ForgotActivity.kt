package com.example.firebasecourse

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityForgotBinding
import com.example.firebasecourse.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotActivity : AppCompatActivity() {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var forgotBinding: ActivityForgotBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forgotBinding= ActivityForgotBinding.inflate(layoutInflater)
        val view=forgotBinding.root
        setContentView(view)

        val userEmail:EditText=findViewById(R.id.editTextEmail)
        val linkButton: Button =findViewById(R.id.emailLinkButton)

        linkButton.setOnClickListener{

            //user's email address
            val uEmail=userEmail.text.toString()
            //send link to users email address provided in the above text
            auth.sendPasswordResetEmail(uEmail).addOnCompleteListener { task->

                if(task.isSuccessful){

                    Toast.makeText(this, "Check your email for password reset link", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}