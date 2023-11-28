package com.example.firebasecourse.ui.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasecourse.R
import com.example.firebasecourse.WorkoutFeedback
import com.example.firebasecourse.adapters.RunAdapter
import com.example.firebasecourse.other.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.example.firebasecourse.other.SortType
import com.example.firebasecourse.other.TrackingUtility
import com.example.firebasecourse.ui.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.AppSettingsDialogHolderActivity
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment:Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

     private val viewModel:MainViewModel by viewModels()
     private lateinit var fab: FloatingActionButton
     private lateinit var runAdapter: RunAdapter
     private lateinit var rvRuns:androidx.recyclerview.widget.RecyclerView
     private lateinit var spFilter: Spinner
     private lateinit var buttonFeedback: Button

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          rvRuns=view.findViewById(R.id.rvRuns)
          spFilter=view.findViewById(R.id.spFilter)
          buttonFeedback=view.findViewById(R.id.buttonFeedback)

          requestPermissions()
          setUpRecyclerView()

          when(viewModel.sortType){
               SortType.DATE->spFilter.setSelection(0)
               SortType.DISTANCE->spFilter.setSelection(2)
               SortType.AVG_SPEED->spFilter.setSelection(3)
               SortType.CALORIES_BURNED->spFilter.setSelection(4)
               SortType.RUNNING_TIME->spFilter.setSelection(1)

          }

          spFilter.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{

               override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                   when(pos){
                        0-> viewModel.sortRuns(SortType.DATE)
                        1-> viewModel.sortRuns(SortType.RUNNING_TIME)
                        2-> viewModel.sortRuns(SortType.DISTANCE)
                        3-> viewModel.sortRuns(SortType.AVG_SPEED)
                        4-> viewModel.sortRuns(SortType.CALORIES_BURNED)

                   }

               }

               override fun onNothingSelected(p0: AdapterView<*>?) {}
          }


          viewModel.runs.observe(viewLifecycleOwner, Observer{
               runAdapter.submitList(it)
          })

          fab=view.findViewById(R.id.fab)
          fab.setOnClickListener {
               findNavController().navigate(R.id.action_runFragment_to_trackingFragment2)
          }

          buttonFeedback.setOnClickListener {
               startActivity(Intent(context, WorkoutFeedback::class.java))
          }

     }



     private fun setUpRecyclerView()=rvRuns.apply{

          runAdapter= RunAdapter()
          adapter=runAdapter
          layoutManager=LinearLayoutManager(requireContext())

     }

     private fun requestPermissions(){
          if(TrackingUtility.hasLocationPermissions(requireContext())){
               return
          }
          if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
               EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions in order to perform a running workout",
                    REQUEST_CODE_LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION


               )
          }else{
               EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions in order to perform a running workout",
                    REQUEST_CODE_LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION

               )

          }
     }

     override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
          if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
               AppSettingsDialog.Builder(this).build().show()
          }else{
               requestPermissions()
          }
     }

     override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) { }

     @Suppress("DEPRECATION")
     override fun onRequestPermissionsResult(
          requestCode: Int,
          permissions: Array<out String>,
          grantResults: IntArray
     ) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
     }
}