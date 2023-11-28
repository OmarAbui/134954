package com.example.firebasecourse.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.firebasecourse.R
import com.example.firebasecourse.other.Constants.KEY_NAME
import com.example.firebasecourse.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment:Fragment(R.layout.fragment_settings) {

    private lateinit var etName:com.google.android.material.textfield.TextInputEditText
    private lateinit var etWeight:com.google.android.material.textfield.TextInputEditText
    private lateinit var btnApplyChanges: Button

    @Inject
    lateinit var sharedPref:SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName=view.findViewById(R.id.etName)
        etWeight=view.findViewById(R.id.etWeight)
        btnApplyChanges=view.findViewById(R.id.btnApplyChanges)

        loadFieldsFromSharedPref()

        btnApplyChanges.setOnClickListener {

            val success=applyChangesToSharedPref()
            if(success){

                Snackbar.make(view, "Changes Saved!", Snackbar.LENGTH_LONG).show()

            }else{

                Snackbar.make(view, "Please fill all fields!", Snackbar.LENGTH_LONG).show()

            }

        }

    }

    private fun loadFieldsFromSharedPref(){

        val name=sharedPref.getString(KEY_NAME,"")
        val weight=sharedPref.getFloat(KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())

    }

    private fun applyChangesToSharedPref(): Boolean{

        val nameText=etName.text.toString()
        val weightText=etWeight.text.toString()

        if(nameText.isEmpty() || weightText.isEmpty()){

            return false

        }
        sharedPref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()

        val toolbarText="Let's go $nameText"
        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.tvToolbarTitle).text=toolbarText

        return true
    }

}