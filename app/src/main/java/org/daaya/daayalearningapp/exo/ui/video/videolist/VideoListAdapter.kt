package org.daaya.daayalearningapp.exo.ui.video.videolist

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.daaya.daayalearningapp.exo.R
import org.daaya.daayalearningapp.exo.databinding.ItemVideoListBinding

class VideoListAdapter(private val itemClickListener: OnItemClickListener?) :
    ListAdapter<String, VideoListAdapter.VideoListViewHolder>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }) {

    private val drawables = listOf(
        R.drawable.male_avatar_1,
        R.drawable.male_avatar_3,
        R.drawable.male_avatar_5,
        R.drawable.male_avatar_6,
        R.drawable.male_avatar_7,
        R.drawable.female_avatar_4,
        R.drawable.male_avatar_8,
        R.drawable.female_avatar_5,
        R.drawable.female_avatar_6,
        R.drawable.female_avatar_1,
        R.drawable.dog_avatar_1,
        R.drawable.bird_avatar_1,
        R.drawable.female_avatar_2,
        R.drawable.male_avatar_4,
        R.drawable.female_avatar_3,
        R.drawable.male_avatar_2,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        val binding = ItemVideoListBinding.inflate(LayoutInflater.from(parent.context))
        return VideoListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        val author = getItem(position)
        holder.textView.text = getItem(position)
        holder.imageView.setImageDrawable(
            ResourcesCompat.getDrawable(holder.imageView.resources, getIconForAuthor(author), null)
        )
        if (VideoListViewHolder.iconsHidden){
            holder.imageView.visibility = GONE
        }
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(holder.bindingAdapterPosition) }
    }

    private fun getIconForAuthor(author:String):Int{
        if (author.startsWith("nirmit brahma", ignoreCase = true)){
            return R.drawable.male_avatar_1
        }
        if (author.startsWith("min joon", ignoreCase = true)){
            return R.drawable.male_avatar_2
        }
        if (author.startsWith("neel", ignoreCase = true)){
            return R.drawable.male_avatar_3
        }
        return R.drawable.dog_avatar_1
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun getItemAt(position: Int): String {
        return getItem(position)
    }

    fun hideIcons(hideIcons: Boolean){
        VideoListViewHolder.iconsHidden = hideIcons
    }

    class VideoListViewHolder(binding: ItemVideoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageViewItemTransform
        val textView: TextView = binding.textViewItemTransform
        companion object {
            var iconsHidden = false
        }
    }
}