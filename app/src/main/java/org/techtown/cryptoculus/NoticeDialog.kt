package org.techtown.cryptoculus

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import org.techtown.cryptoculus.databinding.DialogNoticeBinding
import java.util.*

class NoticeDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogNoticeBinding
    private val layoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }
    private val newsAdapter: NewsAdapter by lazy {
        NewsAdapter()
    }
    var news = ArrayList<ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_notice, null, false)

        setContentView(binding.root)
        init()
    }

    // Dialog layout setting
    private fun init() {
        newsAdapter.news = news
        binding.recyclerView.adapter = newsAdapter

        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.attributes = layoutParams
    }
}