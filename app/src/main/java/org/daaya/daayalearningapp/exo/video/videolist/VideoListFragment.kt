package org.daaya.daayalearningapp.exo.video.videolist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoListBinding
import org.daaya.daayalearningapp.exo.databinding.ItemVideoListBinding
import org.daaya.daayalearningapp.exo.video.VideoActivity

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
class VideoListFragment : Fragment(), VideoListAdapter.OnItemClickListener {

    private var _binding: FragmentVideoListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = VideoListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val videoListViewModel = ViewModelProvider(this).get(VideoListViewModel::class.java)
        videoListViewModel.getAllVideos()
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerviewTransform
        recyclerView.adapter = adapter
        videoListViewModel.videos.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class VideoListViewHolder(binding: ItemVideoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageViewItemTransform
        val textView: TextView = binding.textViewItemTransform
    }


    companion object {
        fun getUrl(filename: String): String {
            return DaayaAndroidApplication.baseUrl + "api/v1/stream/" + filename
            //"http://48.217.169.49:8182/api/v1/stream/video1";
        }
    }

    override fun onItemClick(position: Int) {
        adapter.getItemAt(position)?.apply {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(VideoActivity.ARG_VIDEO, this)
            intent.putExtra(VideoActivity.ARG_VIDEO_URL, getUrl(filename))
            ActivityOptionsCompat.makeBasic();
            ContextCompat.startActivity(requireContext(), intent, null)
        }
    }
}