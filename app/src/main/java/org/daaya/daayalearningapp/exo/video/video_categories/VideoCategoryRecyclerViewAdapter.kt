package org.daaya.daayalearningapp.exo.video.video_categories

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoCategoryBinding
import org.daaya.daayalearningapp.exo.video.video_categories.placeholder.PlaceholderContent.PlaceholderItem
import org.daaya.daayalearningapp.exo.video.videolist.VideoListAdapter.OnItemClickListener
import org.daaya.daayalearningapp.exo.video.videolist.VideoListViewModel


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class VideoCategoryRecyclerViewAdapter(
    private val values: List<String>,
    private val itemClickListener: OnItemClickListener?) :
    RecyclerView.Adapter<VideoCategoryRecyclerViewAdapter.VideoCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCategoryViewHolder {
        return VideoCategoryViewHolder(
            FragmentVideoCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoCategoryViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = capitalizeFirstLetter(item)
        holder.itemView.setOnClickListener{
            itemClickListener?.onItemClick(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class VideoCategoryViewHolder(binding: FragmentVideoCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    private fun capitalizeFirstLetter(input1 :String?): String {
        input1?.apply {
            val input = lowercase()
            val s1: String = input.substring(0, 1).uppercase()
            return s1 + input.substring(1)
        }
        return ""
    }


}