package com.example.firebasecourse.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.firebasecourse.R
import com.example.firebasecourse.databinding.FragmentStatisticsBinding
import com.example.firebasecourse.databinding.MarkerViewBinding
import com.example.firebasecourse.other.CustomMarkerView
import com.example.firebasecourse.other.TrackingUtility
import com.example.firebasecourse.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Document
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private lateinit var tvTotalTime:com.google.android.material.textview.MaterialTextView
    private lateinit var tvTotalDistance:com.google.android.material.textview.MaterialTextView
    private lateinit var tvAverageSpeed:com.google.android.material.textview.MaterialTextView
    private lateinit var tvTotalCalories:com.google.android.material.textview.MaterialTextView
    private lateinit var barChart:com.github.mikephil.charting.charts.BarChart
    private lateinit var buttonPDF: Button

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var overlayTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentView=view

        tvTotalTime=view.findViewById(R.id.tvTotalTime)
        tvTotalDistance=view.findViewById(R.id.tvTotalDistance)
        tvAverageSpeed=view.findViewById(R.id.tvAverageSpeed)
        tvTotalCalories=view.findViewById(R.id.tvTotalCalories)
        barChart=view.findViewById(R.id.barChart)
        buttonPDF=view.findViewById(R.id.buttonPDF)

        overlayTextView = TextView(requireContext())
        overlayTextView.visibility = View.GONE
        overlayTextView.setTextColor(Color.WHITE)
        overlayTextView.textSize = 16f
        overlayTextView.gravity = Gravity.CENTER
        overlayTextView.setBackgroundColor(Color.BLACK)

        val overlayLayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        overlayLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        overlayLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        overlayLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        overlayLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        (barChart.parent as ConstraintLayout).addView(overlayTextView, overlayLayoutParams)

        setUpBarChart()
        subscribeToObservers()

        //Below is in the event of the click of the pdf button
        buttonPDF.setOnClickListener {

            try {
                // converting the fragment to a bitmap image
                val bitmap = Bitmap.createBitmap(fragmentView.width, fragmentView.height, Bitmap.Config.ARGB_8888) // create a bitmap object with the same size as the fragment's view
                val canvas = Canvas(bitmap) // create a canvas object from the bitmap object
                fragmentView.draw(canvas) // draw the fragment's view on the canvas

                // saving the bitmap to a file
                val cacheDir = requireContext().getCacheDir() // get the path to the internal cache directory
                val tempFile = File.createTempFile("myStatistics", ".jpg", cacheDir) // create a temporary file in the cache directory with the prefix "myStatistics" and the suffix ".jpg"
                val outputStream = FileOutputStream(tempFile) // create a file output stream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // compress and write the bitmap to the file
                outputStream.close() // close the stream

                // creating a PDF document object
                val document = PDDocument()

                // creating a PDF page object
                val page = PDPage()

                // adding the page to the document
                document.addPage(page)

                // creating a PDF image object from the image file
                val image = PDImageXObject.createFromFile(tempFile.absolutePath, document)

                // creating a PDF content stream object
                val contentStream = PDPageContentStream(document, page)

                // getting the page size
                val mediaBox = page.mediaBox
                val pageWidth = mediaBox.width
                val pageHeight = mediaBox.height

                // getting the image size
                val imageWidth = image.width.toFloat()
                val imageHeight = image.height.toFloat()

                // calculating the scale factor to fit the image to the page width
                val scaleFactor = pageWidth / imageWidth

                // calculating the scaled image size
                val scaledImageWidth = imageWidth * scaleFactor
                val scaledImageHeight = imageHeight * scaleFactor*1/2

                // calculating the image position to center it vertically on the page
                val imageX = 0f // left edge of the page
                val imageY = (pageHeight - scaledImageHeight)*10 / 29 // middle of the page

                // drawing the image on the page with the scale factor and the position
                contentStream.drawImage(image, imageX, imageY, scaledImageWidth, scaledImageHeight)

                // closing the content stream
                contentStream.close()

                // saving the document to a file
                val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // get the path to the public external storage directory
                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) // create a date format object
                val date = dateFormat.format(Date()) // get the current date and time
                val fileName = "myStatistics_$date.pdf" // create a custom file name that includes the date and time
                val file = File(externalDir, fileName) // create a file object with the subdirectory and the file name
                document.save(file) // save the document to the file

                // closing the document
                document.close()

                // show a toast message to the user
                Toast.makeText(requireContext(), "The file has been saved to $fileName", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(),"Check Your Downloads For The File", Toast.LENGTH_LONG).show()

            } catch (e: IOException) {
                // handle the exception
                Toast.makeText(requireContext(), "An error occurred while saving the file", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }


    }

    private fun setUpBarChart(){

        barChart.xAxis.apply{

            position= XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor= Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)

        }

        barChart.axisLeft.apply {

            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)

        }

        barChart.axisRight.apply {

            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)

        }

        barChart.apply {

            description.text="Y-Axis: Average Speed\n X-Axis: Time"
            description.textSize = 16f
            legend.isEnabled=false

        }

        barChart.setDrawMarkers(true)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // Update the overlay TextView with the selected values
                if (e != null) {
                    val curRunId = e.x.toInt()
                    viewModel.runSortedByDate.value?.let { runs ->
                        if (curRunId >= 0 && curRunId < runs.size) {
                            val run = runs[curRunId]
                            val displayText =
                                "AvgSpeed: ${run.avgSpeedInKMH} Km/h\n" +
                                        "Distance: ${run.distanceInMeters / 1000f} Km\n" +
                                        "Duration: ${TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)}\n" +
                                        "Calories Burned: ${run.caloriesBurned} Kcal"
                            overlayTextView.text = displayText
                            overlayTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onNothingSelected() {
                // Hide the overlay TextView when nothing is selected
                overlayTextView.visibility = View.GONE
            }
        })

        // ... (existing code)
    }





    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer{

            it?.let{

                val totalTimeRun=TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text=totalTimeRun


            }

        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer{

            it?.let{

                val km=it/1000f
                val totalDistance= round(km*10f)/10f
                val totalDistanceString="${totalDistance}Km"
                tvTotalDistance.text=totalDistanceString


            }

        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer{

            it?.let{

                val avgSpeed=round(it*10f)/10f
                val avgSpeedString="${avgSpeed}Km/h"
                tvAverageSpeed.text=avgSpeedString


            }

        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer{

            it?.let{

               val totalCalories="${it}Kcal"
                tvTotalCalories.text=totalCalories

            }

        })

        viewModel.runSortedByDate.observe(viewLifecycleOwner, Observer{
            it?.let{

                    val allAvgSpeeds=it.indices.map { i-> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                    val bardataSet=BarDataSet(allAvgSpeeds, "Y-Axis: Average Speed\n" +
                            " X-Axis: Time").apply {
                        valueTextColor=Color.WHITE
                        color=ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    }

                    barChart.data= BarData(bardataSet)

                    val inflater = LayoutInflater.from(requireActivity())
                    val markerViewBinding = MarkerViewBinding.inflate(inflater)
                    barChart.marker=CustomMarkerView( it,markerViewBinding, requireActivity() ,R.layout.marker_view)
                    barChart.invalidate()
                }

        })


    }
}
