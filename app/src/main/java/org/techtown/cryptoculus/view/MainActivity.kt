package org.techtown.cryptoculus.view

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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.MainViewModel

// CryptOculusMVVM without Database Temporary

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
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // For Retrofit.Call.execute()
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(StrictMode
                .ThreadPolicy
                .Builder()
                .permitAll()
                .build())
        }

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

    override fun onResume() {
        super.onResume()

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

    override fun onStop() {
        super.onStop()

        // 처음 시작했을 때만 restartApp을 삽입
        // 재시작했을 땐 restartApp을 삽입하지 않는다
        // 처음 시작: false, 재시작: true
        if (!getSharedPreferences("restartApp", MODE_PRIVATE).getBoolean("restartApp", false))
            getSharedPreferences("restartApp", MODE_PRIVATE)
                .edit()
                .putBoolean("restartApp", true)
                .apply()

        getSharedPreferences("exchange", MODE_PRIVATE)
                .edit()
                .putString("exchange", mainViewModel.getExchange())
                .apply()
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
                    arrayOf("Coinone", "Bithumb", "Upbit", "Huobi"))

            setSelection(when (mainViewModel.getExchange()) {
                "Coinone" -> 0
                "Bithumb" -> 1
                "Upbit" -> 2
                else -> 3 // "Huobi"
            })
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // viewModel.getData(spinner.getItemAtPosition(position) as String)

                    showLoadingScreen(true)
                    mainViewModel.addDisposable(mainViewModel.getData(spinner.getItemAtPosition(position) as String)
                            .subscribe(
                                    {
                                        mainAdapter.apply {
                                            coinInfos = it
                                            notifyDataSetChanged()
                                        }
                                        changeLayout(spinner.getItemAtPosition(position) as String)
                                        showLoadingScreen(false)
                                    },
                                    { println("response error in getData(\"${spinner.getItemAtPosition(position)}\"): ${it.message}") }
                            ))
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
        mainViewModel = ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)

        binding.apply {
            recyclerView.apply {
                adapter = mainAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            swipeRefreshLayout.setOnRefreshListener {
                showLoadingScreen(true)

                mainViewModel.refreshCoinInfos().subscribe(
                        {
                            mainAdapter.apply {
                                coinInfos = it
                                notifyDataSetChanged()
                            }
                            showLoadingScreen(false)
                        },
                        { println("response error in refreshCoinInfos(): ${it.message}") }
                )
                swipeRefreshLayout.isRefreshing = false
            }
        }

        mainViewModel.getNews().observe(this, { news ->
            openNewsDialog(news)
        })
    }

    fun changeLayout(exchange: String) = supportActionBar!!
            .setBackgroundDrawable(
                    ColorDrawable(
                            when (exchange) {
                                "Coinone" -> 0xFF0079FE.toInt()
                                "Bithumb" -> 0xFFF37321.toInt()
                                "Upbit" -> 0xFF073686.toInt()
                                else -> 0xFF1C2143.toInt() // huobi
                            }
                    )
            )

    private fun openOptionFragment() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
                .beginTransaction()
                .add(R.id.constraintLayout, PreferencesFragment())
                .addToBackStack(null)
                .commit()
    }

    fun backToMainActivity() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

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

    private fun println(data: String) = Log.d("MainAcitivity", data)

    private fun showToast(data: String) = Toast
            .makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
}