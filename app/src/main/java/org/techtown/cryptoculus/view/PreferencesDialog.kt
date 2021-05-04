package org.techtown.cryptoculus.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.databinding.DataBindingUtil
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.DialogPreferencesBinding

class PreferencesDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogPreferencesBinding
    var sortMode = 0 // 0..5
    var mode = 0 // 0..1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.dialog_preferences,
                null,
                false)

        setContentView(binding.root)

        init()
    }

    private fun init() {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.attributes = layoutParams

        binding.apply {
            spinner.apply {
                dropDownWidth = ListPopupWindow.WRAP_CONTENT
                // setSelection(0)
                adapter = ArrayAdapter(
                    mContext,
                    R.layout.item_spinner,
                    arrayOf("코인 이름 ↑", "코인 이름 ↓", "현재가 ↑", "현재가 ↓", "등락률 ↑", "등락률 ↓"))

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        sortMode = position
                        mode = 0
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) { }
                }
            }
            textView2.setOnClickListener {
                mode = 1
                this@PreferencesDialog.dismiss()
            }
        }
    }
}