package com.winclaw.bookrank.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winclaw.bookrank.R
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.databinding.FragmentBookRankBinding
import com.winclaw.bookrank.ui.adapter.BookRankAdapter
import com.winclaw.bookrank.ui.viewmodel.MainViewModel

/**
 * 书籍排行榜 Fragment
 */
class BookRankFragment : Fragment() {
    
    private var _binding: FragmentBookRankBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: BookRankAdapter
    private lateinit var viewModel: MainViewModel
    private var platform: PlatformType? = null
    
    companion object {
        private const val ARG_PLATFORM = "platform"
        
        fun newInstance(platform: PlatformType): BookRankFragment {
            val fragment = BookRankFragment()
            val args = Bundle()
            args.putString(ARG_PLATFORM, platform.platformCode)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        platform = arguments?.getString(ARG_PLATFORM)?.let {
            PlatformType.fromCode(it)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookRankBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = (activity as? MainActivity)?.let {
            it.viewModel
        } ?: return
        
        initRecyclerView()
        observeData()
    }
    
    private fun initRecyclerView() {
        adapter = BookRankAdapter { book ->
            // TODO: 打开书籍详情
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookRankFragment.adapter
        }
    }
    
    private fun observeData() {
        platform?.let { p ->
            viewModel.bookList.observe(viewLifecycleOwner) { books ->
                // 只显示当前平台的书籍
                val platformBooks = books.filter { it.platform == p }
                adapter.submitList(platformBooks)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
