package org.techtown.cryptoculus.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.DialogOptionBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*

class OptionDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogOptionBinding
    private lateinit var optionDialog: OptionDialog
    private val layoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }
    val optionAdapter: OptionAdapter by lazy {
        OptionAdapter()
    }
    lateinit var coinInfos: ArrayList<CoinInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_option, null, false)

        setContentView(binding.root)
        init()
    }

    // Dialog layout setting
    private fun init() {
        optionAdapter.coinInfos = coinInfos
        binding.recyclerView.adapter = optionAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(mContext)

        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.attributes = layoutParams
        optionDialog = this
    }

    private fun setList(coinInfos: ArrayList<CoinInfo>) {
        optionAdapter.coinInfos = coinInfos
    }
}