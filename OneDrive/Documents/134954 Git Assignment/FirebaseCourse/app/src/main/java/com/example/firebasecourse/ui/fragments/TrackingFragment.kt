package com.example.firebasecourse.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.firebasecourse.R
import com.example.firebasecourse.db.Run
import com.example.firebasecourse.other.Constants.ACTION_PAUSE_SERVICE
import com.example.firebasecourse.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.firebasecourse.other.Constants.ACTION_STOP_SERVICE
import com.example.firebasecourse.other.Constants.MAP_ZOOM
import com.example.firebasecourse.other.Constants.POLYLINE_COLOR
import com.example.firebasecourse.other.Constants.POLYLINE_WIDTH
import com.example.firebasecourse.other.TrackingUtility
import com.example.firebasecourse.services.PolyLine
import com.example.firebasecourse.services.TrackingService
import com.example.firebasecourse.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
    private var map:GoogleMap?=null
    private lateinit var mapView: MapView
    private lateinit var btnToggleRun:com.google.android.material.button.MaterialButton
    private lateinit var btnFinishRun:com.google.android.material.button.MaterialButton
    private lateinit var tvTimer:com.google.android.material.textview.MaterialTextView
    private val REQUEST_NOTIFICATION_PERMISSION=1
    private var isTracking=false
    private var pathPoints= mutableListOf<PolyLine>()
    private var curTimeInMillis=0L
    private var menu: Menu?=null

    @set:Inject
     var weight=80f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView=view.findViewById(R.id.mapView)
        btnToggleRun=view.findViewById(R.id.btnToggleRun)
        btnFinishRun= view.findViewById(R.id.btnFinishRun)
        tvTimer=view.findViewById(R.id.tvTimer)

        btnToggleRun.setOnClickListener{

            toggleRun()

          // if(checkNotificationPermission()){

            //sendCommandToService(ACTION_START_OR_RESUME_SERVICE)

           //}else{
              // requestNotificationPermission()
           //}
        }

        btnFinishRun.setOnClickListener{

            zoomToSeeWholeTrack()
            endRunAndSaveToDb()

        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync{

            map=it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
        val permissionStatus = ContextCompat.checkSelfPermission(requireContext(), notificationPermission)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val cameraPermission = Manifest.permission.POST_NOTIFICATIONS
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(cameraPermission), REQUEST_NOTIFICATION_PERMISSION)
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer{
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner,Observer{
            pathPoints=it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner,Observer{

            curTimeInMillis=it
            var formattedTime=TrackingUtility.getFormattedStopWatchTime(curTimeInMillis,true)
            tvTimer.text=formattedTime

        })

    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }


    @Suppress("DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        @Suppress("DEPRECATION")
        super.onPrepareOptionsMenu(menu)

        if(curTimeInMillis>0L){

            this.menu?.getItem(0)?.isVisible=true

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.miCancelTracking->{
                showCancelTrackingDialog()
            }
        }


        @Suppress("DEPRECATION")
        return super.onOptionsItemSelected(item)


    }

    private fun showCancelTrackingDialog(){

        val dialog= MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure you want to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_->
                stopRun()

            }
            .setNegativeButton("No"){dialogInterface,_->

                dialogInterface.cancel()

            }
            .create()
            dialog.show()

    }

    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            btnToggleRun.text="Start"
            btnFinishRun.visibility=View.VISIBLE

        }else{
            btnToggleRun.text="Stop"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack(){

        val bounds=LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height*0.05f).toInt()
            )
        )

    }

    private fun endRunAndSaveToDb(){

        map?.snapshot {bmp->

            var distanceInMeters=0
            for(polyLine in pathPoints){

                distanceInMeters+=TrackingUtility.calculatePolylineLength(polyLine).toInt()

            }

            //Converting distance to km by dividing by 1000
            val avgSpeed= round((distanceInMeters/1000f) /(curTimeInMillis/1000f/60/60) * 10)/10f
            val dateTimestamp=Calendar.getInstance().timeInMillis
            val caloriesBurned=((distanceInMeters/1000f)* weight).toInt()
            val run= Run(bmp, dateTimestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Your run has been saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }

    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng=pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng=pathPoints.last().last()
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action:String)=
        Intent(requireContext(), TrackingService::class.java).also{
            it.action=action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}