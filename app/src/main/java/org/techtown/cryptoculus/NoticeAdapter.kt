package org.techtown.cryptoculus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.databinding.ItemNoticeBinding

// 신규상장, 상장폐지별 리사이클러뷰 어댑터
// item.noticeList 사용
class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    var notices = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoticeBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) = viewHolder.bind(notices[position])

    override fun getItemCount() = notices.size

    inner class ViewHolder(private val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: String) {
            binding.notice = notice
        }
    }
}