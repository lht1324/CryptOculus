package org.techtown.cryptoculus.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.DialogPreferencesBinding

class PreferencesDialog(private val mContext: Context, var sortMode: Int) : Dialog(mContext) {
    private lateinit var binding: DialogPreferencesBinding
    private val layoutParams by lazy {
        WindowManager.LayoutParams()
    }
    var mode = 0 // 0..1
    val sortModeLiveData = MutableLiveData(sortMode)

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
        binding.apply {
            spinner.apply {
                dropDownWidth = ListPopupWindow.WRAP_CONTENT
                adapter = ArrayAdapter(
                    mContext,
                    R.layout.item_spinner_preferences,
                    arrayOf("코인 이름 ↑", "코인 이름 ↓", "현재가 ↑", "현재가 ↓", "등락률 ↑", "등락률 ↓"))

                setSelection(sortMode, false)

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        sortModeLiveData.value = position
                        // onItemSelected 실행되면 value 바뀌면서 자동으로 update가 한 번 더 된다
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) { }
                }
            }
            // textView2.setBackgroundColor(R.drawable.text_click)
            textView2.clicks().subscribe {
                mode = 1
                this@PreferencesDialog.dismiss()
            }
        }

        layoutParams.apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            dimAmount = 0.5f
        }

        window!!.apply{
            attributes = layoutParams
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}