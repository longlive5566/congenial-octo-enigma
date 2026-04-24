package com.winclaw.bookrank.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.ui.fragment.BookRankFragment

/**
 * ViewPager2 适配器
 */
class BookRankViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val platforms: List<PlatformType>
) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = platforms.size
    
    override fun createFragment(position: Int): Fragment {
        return BookRankFragment.newInstance(platforms[position])
    }
}
