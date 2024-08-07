package org.daaya.daayalearningapp.exo.ui.video.videolist

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
import androidx.recyclerview.widget.RecyclerView
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.databinding.FragmentVideoListBinding
import org.daaya.daayalearningapp.exo.ui.video.VideoActivity

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
class VideoListFragment : Fragment(), VideoListAdapter.OnItemClickListener {

    private var _binding: FragmentVideoListBinding? = null
    private lateinit var videoListViewModel: VideoListViewModel
    private lateinit var callback: OnBackPressedCallback
    private lateinit var textView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = VideoListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View {

        videoListViewModel = ViewModelProvider(this)[VideoListViewModel::class.java]
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerviewTransform
        textView = binding.textHeader
        recyclerView.adapter = adapter
        videoListViewModel.itemList.observe(viewLifecycleOwner) {
            adapter.hideIcons(videoListViewModel.hideIcons())
            adapter.submitList(it)
        }
        videoListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        // This callback is only called when Fragment is at least started
        callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val handled = videoListViewModel.handleBackPressed()
            callback.isEnabled = handled
        }
        callback.isEnabled = true

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

    /*
    fun onItemClick1(position: Int) {
        adapter.getItemAt(position)?.apply {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(VideoActivity.ARG_VIDEO, this)
            intent.putExtra(VideoActivity.ARG_VIDEO_URL, getUrl(filename))
            ActivityOptionsCompat.makeBasic();
            ContextCompat.startActivity(requireContext(), intent, null)
        }
    }

     */

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