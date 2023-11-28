package com.example.firebasecourse

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasecourse.databinding.FeedbackItemsBinding

class FeedbackAdapter(
    var context: Context,
    var feedbackList:ArrayList<WorkoutFeedbacks>,


):RecyclerView.Adapter<FeedbackAdapter.feedbackViewHolder>() {

    inner class feedbackViewHolder(val adapterBinding:FeedbackItemsBinding):RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): feedbackViewHolder {
        val binding=FeedbackItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return feedbackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    override fun onBindViewHolder(holder: feedbackViewHolder, position: Int) {
        holder.adapterBinding.feedbackDate.text="Date: "+feedbackList[position].fdate
        holder.adapterBinding.feedbackTime.text="Time: "+feedbackList[position].ftime
        holder.adapterBinding.feedbackContent.text="Feedback:"+feedbackList[position].wfeedback

    }


}