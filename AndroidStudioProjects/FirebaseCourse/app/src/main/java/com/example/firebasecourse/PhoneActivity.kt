package com.example.firebasecourse


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.firebasecourse.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity(){

     lateinit var phoneBinding:ActivityPhoneBinding
    var auth:FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var mCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationCode=""
    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference
    //val userId:String=auth.currentUser!!.uid



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneBinding=ActivityPhoneBinding.inflate(layoutInflater)
        val view=phoneBinding.root
        setContentView(view)

        //val numberView:EditText=findViewById(R.id.editTextPhone)
        //val codeText:EditText=findViewById(R.id.editTextVerifyOTP)
        val sendCode: Button =findViewById(R.id.OTPlinkButton)
        val verifyCode:Button=findViewById(R.id.verifyOTPButton)
        //val userId=intent.getStringExtra("userID")

        var phoneNumber: String? = null


        sendCode.setOnClickListener{
            reference.addValueEventListener(object:ValueEventListener{
                //retrieving phone number from the database
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        phoneNumber=snapshot.child("Users").child(auth.currentUser!!.uid).child("phone").value as String
                        val options=PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber!!)
                            .setTimeout(120L,TimeUnit.SECONDS)
                            .setActivity(this@PhoneActivity)
                            .setCallbacks(mCallBacks)
                            .build()

                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

        verifyCode.setOnClickListener{
            smsCode()

        }

        mCallBacks=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                //Defining actions to be taken when code is verified

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                //Defining actions when code is not correct
                Log.w(TAG, "onVerificationFailed:$p0")

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode=p0
                Log.d(TAG, "onCodeSent:$verificationCode")

            }


        }
    }

    fun smsCode(){

       val userEnterCode=phoneBinding.editTextVerifyOTP.text.toString()
        val credential=PhoneAuthProvider.getCredential(verificationCode,userEnterCode)
        val user = auth.currentUser!!
        val isPhoneNumberProviderLinked = user.providerData.any { it.providerId == PhoneAuthProvider.PROVIDER_ID }

        if(isPhoneNumberProviderLinked){
            signPhoneAuthCredential(credential)
        }
        else{
        user.linkWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Linking successful")

                    signPhoneAuthCredential(credential)


                } else {
                    Log.e(TAG, "Linking failed: ${task.exception?.message}")
                    Toast.makeText(applicationContext, "The Linking did not work", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }

    fun signPhoneAuthCredential(credential: PhoneAuthCredential){

        //var userId=intent.getStringExtra("userID")

       auth.signInWithCredential(credential).addOnCompleteListener{task->

           if(task.isSuccessful){
               Toast.makeText(applicationContext, "Correct Code!!", Toast.LENGTH_SHORT).show()

               //starting main activity
               val intent=Intent(this, MainActivity::class.java)

               //intent.putExtra("userID",userId)
               //intent.putExtra("userIDP",userId)
               startActivity(intent)
           }else{
               Toast.makeText(applicationContext, "The code you entered is incorrect!", Toast.LENGTH_SHORT).show()
           }
       }

    }

}