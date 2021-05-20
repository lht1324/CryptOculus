package org.techtown.cryptoculus.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.MainViewModel
import org.techtown.cryptoculus.viewmodel.SortingViewModel

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    // 고칠 것
    // 파일 저장 권한
    // 터치한 곳에 버튼 넣어서 거래소 차트로 연결해주기
    // 자동으로 새로 받아오기
    private lateinit var binding: ActivityMainBinding
    private lateinit var callback: OnBackPressedCallback
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter(this)
    }
    private var backPressedLast: Long = 0
    // private lateinit var mainViewModel: MainViewModel
    // private lateinit var sortingViewModel: SortingViewModel
    private val mainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }
    private val sortingViewModel by lazy {
        ViewModelProvider(this, SortingViewModel.Factory(application)).get(SortingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        showToast("종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.")
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

    @RequiresApi(Build.VERSION_CODES.N)
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
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option -> openPreferencesDialog()
            // R.id.help -> { }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        binding.apply {
            recyclerView.apply {
                adapter = mainAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            swipeRefreshLayout.setOnRefreshListener {
                getData()
                swipeRefreshLayout.isRefreshing = false
            }

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mainAdapter.apply {
                        if (coinInfos.isNotEmpty()) { // 이 조건을 안 넣으면 비동기 처리 중이라서 어댑터 내의 Array가 비었을 때도 텍스트 변경을 관찰한다.
                            filter.filter(charSequence)
                            filteredCoinInfos = sortingViewModel.sortCoinInfos(filteredCoinInfos)
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }

        // 3초마다 받아오는 기능 만들 때 레이아웃 초기화되는 거 없애야 한다
        mainViewModel.getCoinInfos().observe(this, { coinInfos ->
            mainAdapter.setItems(sortingViewModel.sortCoinInfos(coinInfos))
            showLoadingScreen(false)
        })

        mainViewModel.getNews().observe(this, { news ->
            openNewsDialog(news)
        })

        mainAdapter.openChart.observe(this, { coinName ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(when (mainViewModel.getSelection()) {
                0 -> "https://coinone.co.kr/chart?site=coinone${coinName.toLowerCase()}&unit_time=15m"
                1 -> "https://m.bithumb.com/trade/chart/${coinName}_KRW"
                else -> "https://upbit.com/exchange?code=CRIX.UPBIT.KRW-$coinName&tab=chart"
            })))
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "보고 싶은 코인을 선택하세요."

        supportFragmentManager
                .beginTransaction()
                .add(R.id.constraintLayout, PreferencesFragment(application))
                .addToBackStack(null)
                .commit()
    }

    fun backToMainActivity() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = "CryptOculus"

        showLoadingScreen(true)
        supportFragmentManager.popBackStack()
        getData()
    }

    private fun openNewsDialog(news: ArrayList<Any>) = NewsDialog(this).apply {
        println("openNewsDialog() is executed.")
        if (news[0] == "newList" || news[0] == "deList")
            news.add(news[1])
        else {
            news.add(news[1])
            news.add(news[2])
        }
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

    private fun showLoadingScreen(show: Boolean) = binding.apply {
        if (show) {
            swipeRefreshLayout.visibility = View.GONE

            progressBar.apply {
                isIndeterminate = true
                visibility = View.VISIBLE
            }
        }
        else {
            swipeRefreshLayout.visibility = View.VISIBLE

            progressBar.apply {
                isIndeterminate = false
                visibility = View.GONE
            }
        }
    }

    private fun getData() {
        if (binding.editText.text.isNotBlank())
            binding.editText.text.clear()
        showLoadingScreen(true)
        mainViewModel.getData()
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

    private fun println(data: String) = Log.d("MainAcitivity", data)

    private fun showToast(data: String) = Toast
            .makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
}