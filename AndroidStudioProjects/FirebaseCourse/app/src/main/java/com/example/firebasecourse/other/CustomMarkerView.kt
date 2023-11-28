package com.example.firebasecourse.other

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.firebasecourse.R
import com.example.firebasecourse.databinding.FragmentStatisticsBinding
import com.example.firebasecourse.db.Run
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.firebasecourse.databinding.MarkerViewBinding

class CustomMarkerView(

    val runs:List<Run>,
    val binding:MarkerViewBinding,
    c:Context,
    layoutId:Int,

):MarkerView(c,layoutId) {



    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())

    }


    @SuppressLint("MissingInflatedId", "LogNotTimber")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        Log.d("MarkerView", "refreshContent called")
        super.refreshContent(e, highlight)

        if (e == null) {
            return
        }

        val curRunId = e.x.toInt()
        Log.d("MarkerView", "Clicked bar x-value: $curRunId") // Log the x-value
        if (curRunId >= 0 && curRunId < runs.size) {
            val run = runs[curRunId]

            Log.d("MarkerView2", "Run data: $run")



            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }


            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            binding.tvDateGraph.text = dateFormat.format(calendar.time)

            Log.d("MarkerView3", "No display")
            val avgSpeed = "${run.avgSpeedInKMH}Km/h"
            binding.tvAvgSpeedGraph.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters / 1000f}Km"
            binding.tvDistanceGraph.text = distanceInKm


            binding.tvDurationGraph.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurned}Kcal"
            binding.tvCaloriesBurnedGraph.text = caloriesBurned


            Log.d("MarkerView4", "Timestamp: ${run.timestamp}")
            Log.d("MarkerView5", "AvgSpeed: ${run.avgSpeedInKMH}")
            Log.d("MarkerView6", "Distance: ${run.distanceInMeters}")
            Log.d("MarkerView7", "TimeInMillis: ${run.timeInMillis}")
            Log.d("MarkerView8", "CaloriesBurned: ${run.caloriesBurned}")

        }
        visibility = View.VISIBLE
    }
}

//val inflater = LayoutInflater.from(context)
//val differentLayout = inflater.inflate(R.layout.marker_view, null, false)
// val tvCaloriesBurned = differentLayout.findViewById<TextView>(R.id.tvCaloriesBurnedGraph)
// val tvDuration = differentLayout.findViewById<TextView>(R.id.tvDurationGraph)
// val tvDistance = differentLayout.findViewById<TextView>(R.id.tvDistanceGraph)
//val tvAvgSpeed = differentLayout.findViewById<TextView>(R.id.tvAvgSpeedGraph)
// val tvDate = differentLayout.findViewById<TextView>(R.id.tvDateGraph)