package com.example.firebasecourse.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.firebasecourse.R
import com.example.firebasecourse.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.firebasecourse.other.Constants.KEY_NAME
import com.example.firebasecourse.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref:SharedPreferences

    @set:Inject
    var isFirstAppOpen=true

    private lateinit var tvContinue: TextView
    private lateinit var etName:com.google.android.material.textfield.TextInputEditText
    private lateinit var etWeight:com.google.android.material.textfield.TextInputEditText


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen){

            val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )

        }

        tvContinue = view.findViewById(R.id.tvContinue)
        etName=view.findViewById(R.id.etName)
        etWeight=view.findViewById(R.id.etWeight)


        tvContinue.setOnClickListener{

            val success=writePersonalDataToSharedPref()
            if(success){

                findNavController().navigate(R.id.action_setupFragment_to_runFragment)

            }else{

                Snackbar.make(requireView(), "Please Enter The Fields", Snackbar.LENGTH_SHORT).show()

            }

        }
    }

    private fun writePersonalDataToSharedPref(): Boolean{

           val name=etName.text.toString()
           val weight=etWeight.text.toString()

           if(name.isEmpty() || weight.isEmpty()){

               return false

           }

        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText="Let's go $name!"

        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.tvToolbarTitle).text=toolbarText
        return true

    }

}