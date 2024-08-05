package org.daaya.daayalearningapp.exo.video.video_categories

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
import androidx.lifecycle.ViewModelProvider
import org.daaya.daayalearningapp.exo.databinding.FragmentSlideshowBinding
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoCategoryBinding
import org.daaya.daayalearningapp.exo.video.VideoActivity
import org.daaya.daayalearningapp.exo.video.videolist.VideoListAdapter
import org.daaya.daayalearningapp.exo.video.videolist.VideoListFragment.Companion.getUrl

class VideoCategoriesFragment : Fragment(), VideoListAdapter.OnItemClickListener {
    private lateinit var videoCategoriesViewModel : VideoCategoriesViewModel
    private var _binding: FragmentSlideshowBinding? = null
    private var _binding1: FragmentVideoCategoryBinding? = null
    private lateinit var callback: OnBackPressedCallback

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textView: TextView
    private lateinit var adapter : VideoCategoryRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        _binding1 = FragmentVideoCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        textView = binding.textSlideshow



        videoCategoriesViewModel = ViewModelProvider(this)[VideoCategoriesViewModel::class.java]
        videoCategoriesViewModel.itemList.observe(viewLifecycleOwner) {
            adapter = VideoCategoryRecyclerViewAdapter(it, this)
            binding.recyclerView.adapter = adapter
        }

        videoCategoriesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // This callback is only called when MyFragment is at least started
        callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val handled = videoCategoriesViewModel.handleBackPressed()
            callback.isEnabled = handled
        }
        callback.isEnabled = true
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val listOrString = videoCategoriesViewModel.select(adapter.getItemAt(position))
        if (!listOrString.isList){
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(VideoActivity.ARG_VIDEO_URL, getUrl(listOrString.str))
            ActivityOptionsCompat.makeBasic();
            ContextCompat.startActivity(requireContext(), intent, null)
        }
    }
}