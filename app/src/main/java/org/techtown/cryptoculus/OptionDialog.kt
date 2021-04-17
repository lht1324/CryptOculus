package org.techtown.cryptoculus

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import org.techtown.cryptoculus.databinding.DialogOptionBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*

class OptionDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogOptionBinding
    private lateinit var optionDialog: OptionDialog
    private lateinit var layoutParams: WindowManager.LayoutParams
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

        layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.5f
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = layoutParams
        optionDialog = this
    }

    private fun setList(coinInfos: ArrayList<CoinInfo>) {
        optionAdapter.coinInfos = coinInfos
    }
}