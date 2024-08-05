package org.daaya.daayalearningapp.exo.video.video_categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.daaya.daayalearningapp.exo.databinding.FragmentSlideshowBinding
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoCategoryBinding
import org.daaya.daayalearningapp.exo.video.videolist.VideoListAdapter

class VideoCategoriesFragment : Fragment(), VideoListAdapter.OnItemClickListener {
    private lateinit var videoCategoriesViewModel : VideoCategoriesViewModel
    private var _binding: FragmentSlideshowBinding? = null
    private var _binding1: FragmentVideoCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        _binding1 = FragmentVideoCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        textView = binding.textSlideshow

        videoCategoriesViewModel = ViewModelProvider(this)[VideoCategoriesViewModel::class.java]
        videoCategoriesViewModel.videoTaxonomyDetails.observe(viewLifecycleOwner) {
            textView.text = videoCategoriesViewModel.getCurrentTaxonomyLevel().toString()
            val list = it.taxonomyClasses.keys.toList()
            val adapter = VideoCategoryRecyclerViewAdapter(list, this)
            binding.recyclerView.adapter = adapter
        }

        videoCategoriesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
    }
}