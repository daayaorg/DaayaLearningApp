package org.daaya.daayalearningapp.exo.ui.video.videolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoListBinding
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import org.daaya.daayalearningapp.exo.ui.video.VideoActivity

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
@AndroidEntryPoint
class VideoListFragment : Fragment(), VideoListAdapter.OnItemClickListener {
    private val TAG = "VideoListFragment"
    private var _binding: FragmentVideoListBinding? = null
    private val videoListViewModel: VideoListViewModel by viewModels { VideoListViewModel.Factory}
    private lateinit var callback: OnBackPressedCallback
    private lateinit var textView: TextView
    private lateinit var swipeToRefresh : SwipeRefreshLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = VideoListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerviewTransform
        textView = binding.textHeader
        swipeToRefresh = binding.swipeRefresh
        binding.progressBar.visibility = View.GONE
        recyclerView.adapter = adapter
        videoListViewModel.itemList.observe(viewLifecycleOwner) {
            adapter.hideIcons(videoListViewModel.hideIcons())
            adapter.submitList(it)
            binding.progressBar.visibility = View.GONE
        }

        videoListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        videoListViewModel.progressBarVisibility.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }


        // This callback is only called when Fragment is at least started
        callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val handled = videoListViewModel.handleBackPressed()
            callback.isEnabled = handled
        }
        callback.isEnabled = true


        swipeToRefresh.setOnRefreshListener {
            videoListViewModel.refresh(true)
            swipeToRefresh.isRefreshing = false
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun getUrl(filename: String): String {
            return DaayaAndroidApplication.baseUrl + "api/v1/stream/" + filename
        }
    }

    override fun onItemClick(position: Int) {
        val listOrString = videoListViewModel.select(adapter.getItemAt(position))
        if (!listOrString.isList){
            val video = videoListViewModel.getVideo()
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(VideoActivity.ARG_VIDEO_URL, getUrl(listOrString.str))
            video?.apply {
                intent.putExtra(VideoActivity.ARG_VIDEO, video)
            }
            ActivityOptionsCompat.makeBasic();
            ContextCompat.startActivity(requireContext(), intent, null)
        }
    }
}