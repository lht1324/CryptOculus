package org.techtown.cryptoculus.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.widget.textChanges
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.FirestoreViewModel
import org.techtown.cryptoculus.viewmodel.ImageViewModel
import org.techtown.cryptoculus.viewmodel.MainViewModel
import org.techtown.cryptoculus.viewmodel.SortingViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var callback: OnBackPressedCallback
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter(this)
    }
    private var backPressedLast: Long = 0
    private val mainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }
    private val sortingViewModel by lazy {
        ViewModelProvider(this, SortingViewModel.Factory(application)).get(SortingViewModel::class.java)
    }
    private val imageViewModel by lazy {
        ViewModelProvider(this, ImageViewModel.Factory(application)).get(ImageViewModel::class.java)
    }
    private val firestoreViewModel by lazy {
        ViewModelProvider(this, FirestoreViewModel.Factory(application)).get(FirestoreViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // For Retrofit.Call.execute()
        StrictMode.setThreadPolicy(StrictMode
            .ThreadPolicy
            .Builder()
            .permitAll()
            .build())

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        init()
    }

    fun onBackPressedMain() {
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            finish()
            return
        }

        Toast.makeText(
            this@MainActivity,
            "종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.",
            Toast.LENGTH_SHORT
        ).show()

        backPressedLast = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressedMain()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onPause() {
        super.onPause()

        callback.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        val item = menu?.findItem(R.id.spinner)
        val spinner = item?.actionView as AppCompatSpinner

        spinner.apply {
            dropDownWidth = ListPopupWindow.WRAP_CONTENT
            adapter = ArrayAdapter(
                    this@MainActivity,
                    R.layout.item_spinner_actionbar,
                    arrayOf("Coinone", "Bithumb", "Upbit"))

            // onItemSelectedListener 자동 실행 방지를 위해 animate를 false로
            setSelection(mainViewModel.getSelection(), false)
            changeLayout(mainViewModel.getSelection())

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (binding.editText.text.isNotEmpty())
                        binding.editText.text.clear()
                    changeExchange(position)
                    changeLayout(position)
                    binding.recyclerView.adapter = mainAdapter // 거래소를 바꿨을 때 기존 데이터가 보여지는 현상 방지

                    // 검색창에 커서가 찍혔을 때 거래소를 바꾸면 커서와 키보드를 제거
                    if (binding.editText.isFocused) {
                        if ((getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).isAcceptingText)
                            closeKeyboard()
                        binding.editText.clearFocus()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option -> openPreferencesDialog()
            R.id.help -> startActivity(Intent(this, HelpActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        mainAdapter.setHasStableIds(true)
        showLoadingScreen(true)
        
        binding.apply {
            recyclerView.apply {
                adapter = mainAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            swipeRefreshLayout.setOnRefreshListener {
                notIdleExecute()
                swipeRefreshLayout.isRefreshing = false

                if (binding.editText.isFocused) {
                    if ((getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).isAcceptingText)
                        closeKeyboard()
                    binding.editText.clearFocus()
                }
            }

            editText.apply {
                textChanges().subscribe {
                    mainAdapter.apply {
                        if (it.isNotBlank())
                            mainViewModel.changeIdleCheck()

                        if (coinInfos.isNotEmpty()) {
                            filter.filter(it)
                            filteredCoinInfos = sortingViewModel.sortCoinInfos(filteredCoinInfos)
                        }
                    }
                }

                setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_ENTER }
            }
        }

        mainViewModel.getCoinInfos().observe(this, { coinInfos ->
            mainAdapter.setItems(sortingViewModel.sortCoinInfos(coinInfos))
            if ((coinInfos.size > 0 && coinInfos[0].coinNameKorean.isNotEmpty()) || coinInfos.size == 0)
                showLoadingScreen(false)
        })

        mainViewModel.getDownloadImageByName().observe(this, { coinName ->
            imageViewModel.downloadImage(coinName)
        })

        mainViewModel.getDownloadEveryImage().observe(this, { _ ->
            imageViewModel.downloadEveryImage()
        })

        mainViewModel.getUpdateKoreanByName().observe(this, { coinName ->
            firestoreViewModel.getCoinNameKoreanFirestore(coinName)
        })

        mainViewModel.getUpdateKoreans().observe(this, { _ ->
            firestoreViewModel.getCoinNameKoreansFirestore()
        })

        mainAdapter.openChart.observe(this, { coinName ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(when (mainViewModel.getSelection()) {
                0 -> "https://coinone.co.kr/chart?site=coinone${coinName.lowercase()}&unit_time=15m"
                1 -> "https://m.bithumb.com/trade/chart/${coinName}_KRW"
                else -> "https://upbit.com/exchange?code=CRIX.UPBIT.KRW-$coinName&tab=chart"
            })))
        })

        mainAdapter.clickedItem.observe(this, { coinName ->
            mainViewModel.updateClicked(coinName)
        })
    }

    fun changeLayout(position: Int) = supportActionBar!!
            .setBackgroundDrawable(
                    ColorDrawable(
                            when (position) {
                                0 -> Color.rgb(0, 121, 254)
                                1 -> Color.rgb(243, 115, 33)
                                else -> Color.rgb(7, 54, 134) // 2
                            }
                    )
            )

    private fun openPreferencesFragment() {
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "보고 싶은 화폐를 선택하세요."
        }

        supportFragmentManager
                .beginTransaction()
                .add(R.id.constraintLayout, ChoiceFragment(application))
                .addToBackStack(null)
                .commit()
    }

    fun backToMainActivity() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = "CryptOculus"

        showLoadingScreen(true)
        supportFragmentManager.popBackStack()
        notIdleExecute()
    }

    private fun openPreferencesDialog() = PreferencesDialog(this, mainViewModel.getSortMode()).apply {
        setOnDismissListener {
            if (mode == 1)
                openPreferencesFragment()
        }
        setCancelable(true)
        show()
        sortModeLiveData.observe(this@MainActivity, {
            if (it != mainViewModel.getSortMode())
                updateSortMode(it)
        })
    }

    private fun notIdleExecute() {
        if (binding.editText.text.isNotBlank())
            binding.editText.text.clear()
        showLoadingScreen(true)
        mainViewModel.changeIdleCheck()
    }

    private fun updateSortMode(sortMode: Int) {
        showLoadingScreen(true)
        binding.recyclerView.adapter = mainAdapter
        mainViewModel.updateSortMode(sortMode)
    }

    private fun changeExchange(position: Int) {
        showLoadingScreen(true)
        binding.recyclerView.adapter = mainAdapter // 리사이클러뷰 거래소 첫 부분부터 보여주기
        mainViewModel.changeExchange(position)
    }

    private fun showLoadingScreen(show: Boolean) = binding.apply {
        if (show) {
            swipeRefreshLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE

            progressBar.apply {
                isIndeterminate = true
                visibility = View.VISIBLE
            }
        }
        else {
            swipeRefreshLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE

            progressBar.apply {
                isIndeterminate = false
                visibility = View.GONE
            }
        }
    }

    private fun closeKeyboard() = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

    private fun println(data: String) = Log.d("MainAcitivity", data)
}