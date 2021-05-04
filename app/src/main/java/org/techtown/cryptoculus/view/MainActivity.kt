package org.techtown.cryptoculus.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    // 고칠 것
    // 터치한 곳에 버튼 넣어서 거래소 차트로 연결해주기
    // 검색
    // 정렬
    private lateinit var binding: ActivityMainBinding
    private lateinit var callback: OnBackPressedCallback
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter()
    }
    private var backPressedLast: Long = 0
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate() is executed.")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        val item = menu?.findItem(R.id.spinner)
        val spinner = item?.actionView as AppCompatSpinner

        spinner.apply {
            dropDownWidth = ListPopupWindow.WRAP_CONTENT
            adapter = ArrayAdapter(
                    this@MainActivity,
                    R.layout.item_spinner,
                    arrayOf("Coinone", "Bithumb", "Upbit"))

            setSelection(viewModel.getSelection())

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showLoadingScreen(true)
                    changeLayout(position)
                    viewModel.changeExchange(position)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option -> openOptionFragment()
            // R.id.help -> { }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        viewModel = ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)

        binding.apply {
            recyclerView.apply {
                adapter = mainAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            swipeRefreshLayout.setOnRefreshListener {
                showLoadingScreen(true)

                viewModel.refreshCoinInfos()

                swipeRefreshLayout.isRefreshing = false
            }
        }

        // 3초마다 받아오는 기능 만들 때 레이아웃 초기화되는 거 없애야 한다
        viewModel.getCoinInfos().observe(this, { coinInfos ->
            mainAdapter.coinInfos = coinInfos
            mainAdapter.notifyDataSetChanged()
            showLoadingScreen(false)
        })

        viewModel.getNews().observe(this, { news ->
            openNewsDialog(news)
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

    private fun openOptionFragment() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
                .beginTransaction()
                .add(R.id.constraintLayout, PreferencesFragment(application))
                .addToBackStack(null)
                .commit()
    }

    fun backToMainActivity() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        showLoadingScreen(true)
        supportFragmentManager.popBackStack()
    }

    private fun openNewsDialog(news: ArrayList<Any>) = NewsDialog(this).apply {
        if (news[0] == "newList" || news[0] == "deList")
            news.add(news[1])
        else {
            news.add(news[1])
            news.add(news[2])
        }
    }

    private fun showLoadingScreen(show: Boolean) = binding.apply {
        if (show) {
            constraintLayout.visibility = View.GONE

            progressBar.apply {
                isIndeterminate = true
                visibility = View.VISIBLE
            }
        }
        else {
            constraintLayout.visibility = View.VISIBLE

            progressBar.apply {
                isIndeterminate = false
                visibility = View.GONE
            }
        }
    }

    private fun openPreferencesDialog() {
        val preferencesDialog = PreferencesDialog(this)
        preferencesDialog.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setOnDismissListener {
                when (preferencesDialog.mode) {

                    1 -> openOptionFragment()
                }
            }
            setCancelable(true)
            show()
        }
    }

    private fun println(data: String) = Log.d("MainAcitivity", data)

    private fun showToast(data: String) = Toast
            .makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
}