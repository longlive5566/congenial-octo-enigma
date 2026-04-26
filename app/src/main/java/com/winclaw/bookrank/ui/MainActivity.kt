package com.winclaw.bookrank.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.winclaw.bookrank.R

/**
 * 主界面 - 显示书籍排行榜
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        recyclerView = findViewById(R.id.recyclerView)
        emptyText = findViewById(R.id.emptyText)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BookRankAdapter(emptyList())
        
        // 显示欢迎信息
        emptyText.text = "📚 欢迎使用书籍排行榜\n\n点击底部按钮开始爬取数据"
    }
}

/**
 * 简单的书籍列表适配器
 */
class BookRankAdapter(private var books: List<String>) : RecyclerView.Adapter<BookRankAdapter.ViewHolder>() {
    
    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        textView.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        textView.padding = 16
        return ViewHolder(textView)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = books.getOrNull(position) ?: "书籍 ${position + 1}"
    }
    
    override fun getItemCount() = books.size
    
    fun updateBooks(newBooks: List<String>) {
        books = newBooks
        notifyDataSetChanged()
    }
}
