package com.example.firebasecourse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityLoginBinding
import com.example.firebasecourse.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var loginBinding: ActivityLoginBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding= ActivityLoginBinding.inflate(layoutInflater)
        val view=loginBinding.root
        setContentView(view)

        val regButton: Button =findViewById(R.id.dontHaveAnAccountButton)
        val loginButton:Button=findViewById(R.id.loginButton)
        val userEmail:EditText=findViewById(R.id.editTextMail)
        val userPassword:EditText=findViewById(R.id.editTextPword)
        val forgotButton:Button=findViewById(R.id.forgotPword)

        loginButton.setOnClickListener{
            val uEmail=userEmail.text.toString()
            val uPassword=userPassword.text.toString()
            login(uEmail,uPassword)

        }

        regButton.setOnClickListener{
            val intent2= Intent(this, Register::class.java)
            startActivity(intent2)
        }

        forgotButton.setOnClickListener{
            startActivity(Intent(this, ForgotActivity::class.java))
        }


    }

    fun login(email:String, password:String){

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){

                Toast.makeText(applicationContext, "Successful Login!", Toast.LENGTH_SHORT).show()
                val intent=Intent(this, MainActivity::class.java)
                //val userId:String=auth.currentUser!!.uid
                //intent.putExtra("userID",userId)
                startActivity(intent)
                finish()

            }else{
                //Reason for the error
                Toast.makeText(applicationContext, "Email/Password is Incorrect", Toast.LENGTH_SHORT).show()

            }
        }

    }
}