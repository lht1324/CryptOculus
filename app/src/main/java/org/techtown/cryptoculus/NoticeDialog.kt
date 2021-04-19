package org.techtown.cryptoculus

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import org.techtown.cryptoculus.databinding.DialogNoticeBinding
import org.techtown.cryptoculus.databinding.DialogOptionBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*

class NoticeDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogNoticeBinding
    private val layoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }
    private val noticeAdapterNewListed: NoticeAdapter by lazy {
        NoticeAdapter()
    }
    private val noticeAdapterDeListed: NoticeAdapter by lazy {
        NoticeAdapter()
    }
    lateinit var notices: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_notice, null, false)

        setContentView(binding.root)
        init()
    }

    // Dialog layout setting
    private fun init() {
        noticeAdapter.notices = notices
        binding.recyclerView.adapter = noticeAdapter

        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.attributes = layoutParams
    }
}