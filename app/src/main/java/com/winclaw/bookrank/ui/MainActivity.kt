package com.winclaw.bookrank.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.TabLayoutMediator
import com.google.android.material.tabs.TabLayout
import com.winclaw.bookrank.R
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.databinding.ActivityMainBinding
import com.winclaw.bookrank.ui.adapter.BookRankViewPagerAdapter
import com.winclaw.bookrank.ui.viewmodel.MainViewModel
import com.winclaw.bookrank.data.worker.RankScheduler

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    private val platforms = PlatformType.values().toList()
    private lateinit var viewPagerAdapter: BookRankViewPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initToolbar()
        initTabs()
        initButtons()
        initSearch()
        
        // 观察 UI 状态
        observeUiState()
    }
    
    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }
    
    private fun initTabs() {
        // 设置 ViewPager2 适配器
        viewPagerAdapter = BookRankViewPagerAdapter(this, platforms)
        binding.viewPager.adapter = viewPagerAdapter
        
        // 连接 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = platforms[position].displayName
        }.attach()
        
        // 监听 Tab 切换
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.switchPlatform(platforms[tab.position])
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    private fun initButtons() {
        // 爬取按钮
        binding.btnCrawl.setOnClickListener {
            viewModel.startCrawl()
        }
        
        // 全部爬取按钮
        binding.btnCrawlAll.setOnClickListener {
            viewModel.crawlAllPlatforms()
        }
        
        // 刷新按钮
        binding.btnRefresh.setOnClickListener {
            viewModel.crawlAllPlatforms()
        }
        
        // 设置按钮
        binding.btnSettings.setOnClickListener {
            // TODO: 打开设置页面
            Toast.makeText(this, "设置功能开发中", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.search(it)
                    binding.searchView.clearFocus()
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.clearSearch()
                }
                return true
            }
        })
    }
    
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is MainViewModel.UiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                }
                is MainViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is MainViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is MainViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        // 启动时触发一次定时任务检查
        RankScheduler(this).triggerImmediateUpdate()
    }
}
