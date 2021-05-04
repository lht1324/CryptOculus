package org.techtown.cryptoculus.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.DialogNewsBinding
import java.util.*

class NewsDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogNewsBinding
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
                R.layout.dialog_news, null, false)

        setContentView(binding.root)
        init()
    }

    // Dialog layout setting
    private fun init() {
        newsAdapter.news = news
        binding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false)
        }

        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.apply {
            attributes = layoutParams
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}