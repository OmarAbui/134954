package com.example.firebasecourse.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasecourse.R
import com.example.firebasecourse.db.Run
import com.example.firebasecourse.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter:RecyclerView.Adapter<RunAdapter.RunViewHolder>() {



    inner class RunViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.ivRunImage)
        val dateView:com.google.android.material.textview.MaterialTextView=itemView.findViewById(R.id.tvDate)
        val speedView:com.google.android.material.textview.MaterialTextView=itemView.findViewById(R.id.tvAvgSpeed)
        val distanceView:com.google.android.material.textview.MaterialTextView=itemView.findViewById(R.id.tvDistance)
        val timeView:com.google.android.material.textview.MaterialTextView=itemView.findViewById(R.id.tvTime)
        val caloriesView:com.google.android.material.textview.MaterialTextView=itemView.findViewById(R.id.tvCalories)
    }

    val diffCallback=object:DiffUtil.ItemCallback<Run>(){

        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {

            return oldItem.id==newItem.id

        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {

            return oldItem.hashCode()==newItem.hashCode()

        }

    }

    val differ= AsyncListDiffer(this, diffCallback)
    fun submitList(list:List<Run>)=differ.submitList(list)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(
            R.layout.item_run,
            parent,
            false
        )
        return RunViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {

        val run=differ.currentList[position]
        holder.itemView.apply {

            val ivRunImage=holder.imageView
            Glide.with(this).load(run.img).into(ivRunImage)

            val calendar= Calendar.getInstance().apply{
                timeInMillis=run.timestamp
            }

            val dateFormat=SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            val tvDate=holder.dateView
            tvDate.text=dateFormat.format(calendar.time)

            val avgSpeed="${run.avgSpeedInKMH}Km/h"
            val tvAvgSpeed=holder.speedView
            tvAvgSpeed.text=avgSpeed

            val distanceInKm="${run.distanceInMeters/1000f}Km"
            val tvDistance=holder.distanceView
            tvDistance.text=distanceInKm

            val tvTime=holder.timeView
            tvTime.text=TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned="${run.caloriesBurned}Kcal"
            val tvCalories=holder.caloriesView
            tvCalories.text=caloriesBurned


        }

    }

    override fun getItemCount(): Int {

        return differ.currentList.size

    }
}