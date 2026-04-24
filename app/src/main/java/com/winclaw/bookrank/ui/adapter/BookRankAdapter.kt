package com.winclaw.bookrank.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winclaw.bookrank.R
import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType

/**
 * 书籍列表适配器
 */
class BookRankAdapter(
    private val onItemClick: (BookRank) -> Unit
) : ListAdapter<BookRank, BookRankAdapter.BookRankViewHolder>(BookDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookRankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_rank, parent, false)
        return BookRankViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: BookRankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BookRankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankTv: TextView = itemView.findViewById(R.id.tv_rank)
        private val titleTv: TextView = itemView.findViewById(R.id.tv_title)
        private val authorTv: TextView = itemView.findViewById(R.id.tv_author)
        private val platformTv: TextView = itemView.findViewById(R.id.tv_platform)
        private val heatTv: TextView = itemView.findViewById(R.id.tv_heat)
        private val scoreTv: TextView = itemView.findViewById(R.id.tv_score)
        private val wordCountTv: TextView = itemView.findViewById(R.id.tv_word_count)
        private val coverIv: ImageView = itemView.findViewById(R.id.iv_cover)
        private val platformIndicator: View = itemView.findViewById(R.id.platform_indicator)
        
        fun bind(book: BookRank) {
            // 排名
            rankTv.text = "#${book.rank}"
            rankTv.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rank_badge)
            
            // 书名
            titleTv.text = book.title
            
            // 作者
            authorTv.text = "作者：${book.author}"
            
            // 平台
            platformTv.text = book.platform.displayName
            platformTv.setTextColor(getPlatformColor(book.platform))
            
            // 热度
            book.heatValue?.let {
                heatTv.text = "热度：${formatNumber(it)}"
                heatTv.visibility = View.VISIBLE
            } ?: run {
                heatTv.visibility = View.GONE
            }
            
            // 评分
            book.score?.let {
                scoreTv.text = "评分：${String.format("%.1f", it)}"
                scoreTv.visibility = View.VISIBLE
            } ?: run {
                scoreTv.visibility = View.GONE
            }
            
            // 字数
            book.wordCount?.let {
                wordCountTv.text = "${formatWordCount(it)}"
                wordCountTv.visibility = View.VISIBLE
            } ?: run {
                wordCountTv.visibility = View.GONE
            }
            
            // 封面
            book.coverUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(coverIv)
            } ?: run {
                coverIv.setImageResource(R.drawable.ic_book_placeholder)
            }
            
            // 平台指示条
            platformIndicator.setBackgroundColor(getPlatformColor(book.platform))
            
            // 点击事件
            itemView.setOnClickListener {
                onItemClick(book)
            }
        }
        
        private fun getPlatformColor(platform: PlatformType): Int {
            return when (platform) {
                PlatformType.QIANDIAN -> ContextCompat.getColor(itemView.context, R.color.qidian_color)
                PlatformType.JINJIANG -> ContextCompat.getColor(itemView.context, R.color.jinjiang_color)
                PlatformType.DOUBAN -> ContextCompat.getColor(itemView.context, R.color.douban_color)
                PlatformType.FANQIE -> ContextCompat.getColor(itemView.context, R.color.fanqie_color)
                else -> ContextCompat.getColor(itemView.context, R.color.primary)
            }
        }
        
        private fun formatNumber(num: Long): String {
            return when {
                num >= 100000000 -> String.format("%.1f 亿", num / 100000000.0)
                num >= 10000 -> String.format("%.1f 万", num / 10000.0)
                else -> num.toString()
            }
        }
        
        private fun formatWordCount(count: Long): String {
            return if (count >= 10000) {
                String.format("%.0f 万字", count / 10000.0)
            } else {
                "$count 字"
            }
        }
    }
    
    /**
     * DiffUtil 回调
     */
    class BookDiffCallback : DiffUtil.ItemCallback<BookRank>() {
        override fun areItemsTheSame(oldItem: BookRank, newItem: BookRank): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: BookRank, newItem: BookRank): Boolean {
            return oldItem == newItem
        }
    }
}
