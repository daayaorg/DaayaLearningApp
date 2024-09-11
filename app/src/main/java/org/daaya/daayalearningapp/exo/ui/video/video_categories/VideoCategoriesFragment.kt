package org.daaya.daayalearningapp.exo.ui.video.video_categories

import android.content.Intent
import android.os.Bundle
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoCategoriesBinding
import org.daaya.daayalearningapp.exo.ui.video.VideoActivity
import org.daaya.daayalearningapp.exo.ui.video.videolist.VideoListAdapter
import org.daaya.daayalearningapp.exo.ui.video.videolist.VideoListFragment.Companion.getUrl

@AndroidEntryPoint
class VideoCategoriesFragment : Fragment(), VideoListAdapter.OnItemClickListener {
    private val videoCategoriesViewModel : VideoCategoriesViewModel by viewModels{VideoCategoriesViewModel.Factory}
    private var _binding: FragmentVideoCategoriesBinding? = null
    private lateinit var callback: OnBackPressedCallback

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textView: TextView
    private lateinit var adapter : VideoCategoryRecyclerViewAdapter
    private lateinit var swipeToRefresh : SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentVideoCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        textView = binding.textSlideshow
        swipeToRefresh = binding.swipeRefresh
        binding.progressBar.visibility = View.GONE

        //videoCategoriesViewModel = ViewModelProvider(this)[VideoCategoriesViewModel::class.java]
        videoCategoriesViewModel.itemList.observe(viewLifecycleOwner) {
            adapter = VideoCategoryRecyclerViewAdapter(it, this)
            binding.recyclerView.adapter = adapter
            binding.progressBar.visibility = View.GONE
        }

        videoCategoriesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        videoCategoriesViewModel.progressBarVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        // This callback is only called when Fragment is at least started
        callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val handled = videoCategoriesViewModel.handleBackPressed()
            callback.isEnabled = handled
        }
        callback.isEnabled = true

        swipeToRefresh.setOnRefreshListener {
            videoCategoriesViewModel.refresh(true)
            swipeToRefresh.isRefreshing = false
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val listOrString = videoCategoriesViewModel.select(adapter.getItemAt(position))
        if (!listOrString.isList){
            val video = videoCategoriesViewModel.getVideo()
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