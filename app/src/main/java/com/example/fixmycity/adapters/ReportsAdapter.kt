package com.example.fixmycity.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fixmycity.R
import com.example.fixmycity.databinding.ItemReportBinding
import com.example.fixmycity.models.HazardReport
import com.example.fixmycity.utils.SignalManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsAdapter(
    private val currentUserId: String,
    private var reportsList: List<HazardReport> = emptyList(),
    private val onUpvoteClickListener: (HazardReport) -> Unit
)  : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportsList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvCategory.text = report.category
            tvLocation.text = "${report.cityName}, ${report.neighborhood}"
            tvAddress.text = report.address
            tvDescription.text = report.description
            tvUpVotedCount.text = report.upVotedCount.toString()

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvTimestamp.text = sdf.format(Date(report.timestamp))

            tvStatus.text = report.status

            Glide.with(context)
                .load(report.imageUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(ivReportImage)

            val isOwner = report.reporterUserId == currentUserId && currentUserId.isNotEmpty()
            val isUpvoted = report.upVotedUserIds.contains(currentUserId)

            val grayColor = androidx.core.content.ContextCompat.getColor(context, R.color.border_gray)
            val blueColor = androidx.core.content.ContextCompat.getColor(context, R.color.light_dark_blue)

            if (isUpvoted) {
                ImageViewCompat.setImageTintList(btnUpvote, ColorStateList.valueOf(blueColor))
            } else {
                ImageViewCompat.setImageTintList(btnUpvote, ColorStateList.valueOf(grayColor))
            }

            llUpvote.setOnClickListener {
                if (isOwner) {
                    SignalManager.getInstance().toast("זהו דיווח שאתה יצרת - לא ניתן להצביע לו")
                } else {
                    onUpvoteClickListener(report)
                }
            }
        }
    }

    override fun getItemCount(): Int = reportsList.size

    fun updateReports(newReports: List<HazardReport>) {
        this.reportsList = newReports
        notifyDataSetChanged()
    }
}