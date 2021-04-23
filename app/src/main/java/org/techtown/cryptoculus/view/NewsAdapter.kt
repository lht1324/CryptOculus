package org.techtown.cryptoculus.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.databinding.ItemNewsBinding

// 신규상장과 상장폐지를 담는 어댑터
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    var news = ArrayList<ArrayList<String>>()
    val noticeAdapters = ArrayList<NoticeAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsBinding.inflate(inflater, parent, false)
        noticeAdapters.add(NoticeAdapter())

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) = viewHolder.bind(news[position], position)

    override fun getItemCount() = news.size

    inner class ViewHolder(
            private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notices: ArrayList<String>, position: Int) {
            // 0, 1
            // 이거에 맞춰서 신규 상장이랑 상장 폐지 써 줘야 한다
            binding.textView.text = binding.root.resources.getString(
                    binding.root.resources.getIdentifier(
                            "news$position",
                            "string",
                            "org.techtown.cryptoculus"
                    )
            )
            noticeAdapters[position].notices = notices
            binding.recyclerView.adapter = noticeAdapters[position]
        }
    }
}