package org.daaya.daayalearningapp.exo.video.videolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.daaya.daayalearningapp.exo.R
import org.daaya.daayalearningapp.exo.databinding.ItemVideoListBinding
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo

class VideoListAdapter(private val itemClickListener: OnItemClickListener?) :
    ListAdapter<DaayaVideo, VideoListFragment.VideoListViewHolder>(object : DiffUtil.ItemCallback<DaayaVideo>() {
        override fun areItemsTheSame(oldItem: DaayaVideo, newItem: DaayaVideo): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DaayaVideo, newItem: DaayaVideo): Boolean =
            oldItem == newItem
    }) {

    private val drawables = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6,
        R.drawable.avatar_7,
        R.drawable.avatar_8,
        R.drawable.avatar_9,
        R.drawable.avatar_10,
        R.drawable.avatar_11,
        R.drawable.avatar_12,
        R.drawable.avatar_13,
        R.drawable.avatar_14,
        R.drawable.avatar_15,
        R.drawable.avatar_16,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListFragment.VideoListViewHolder {
        val binding = ItemVideoListBinding.inflate(LayoutInflater.from(parent.context))
        return VideoListFragment.VideoListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: VideoListFragment.VideoListViewHolder, position: Int) {
        val video = getItem(position)
        holder.textView.text = video.title
        holder.imageView.setImageDrawable(
            ResourcesCompat.getDrawable(holder.imageView.resources, drawables[position], null)
        )
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(holder.bindingAdapterPosition) }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun getItemAt(position: Int): DaayaVideo? {
        return getItem(position)
    }
}