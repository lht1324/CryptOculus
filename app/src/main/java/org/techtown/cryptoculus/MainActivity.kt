package org.techtown.cryptoculus

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.ViewModel

class MainActivity : AppCompatActivity() {
    // 고칠 것
    // 옵션 액티비티가 아니라 다이얼로그로 띄워보자
    // 레이아웃을 constraintLayout으로 바꿔보자
    // MVVM
    // Rx 사용
    // 터치하기 전엔 간단한 정보만 보여주다가 터치하면 상세 정보
    // 터치한 곳에 버튼 넣어서 거래소 차트로 연결해주기
    
    /* private val coinoneUrl = "https://api.coinone.co.kr/"
    private val bithumbUrl = "https://api.bithumb.co.kr/"
    private val huobiUrl = "https://api-cloud.huobi.co.kr/"
    private val upbitUrl = "https://api.upbit.com/v1/"
    private var url = coinoneUrl */
    private var exchange = "coinone"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private val adapter = MainAdapter()
    private var backPressedLast: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        init()
    }

    override fun onBackPressed() {
        // Exit if touch once more before 2 secs
        // 2초 전에 누르면 종료
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            getSharedPreferences("restartApp", MODE_PRIVATE)
                .edit()
                .putBoolean("restartApp", true)
                .apply()

            finish()
            return
        }

        Toast.makeText(this, "종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
        backPressedLast = System.currentTimeMillis()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.coinone -> {
                viewModel.publishSubject.onNext("coinone")
                viewModel.publishSubject.onComplete()
                changeLayout("coinone")
            }
            R.id.bithumb -> {
                viewModel.publishSubject.onNext("bithumb")
                viewModel.publishSubject.onComplete()
                changeLayout("bithumb")
            }
            R.id.upbit -> {
                viewModel.publishSubject.onNext("upbit")
                viewModel.publishSubject.onComplete()
                changeLayout("upbit")
            }
            R.id.huobi -> {
                viewModel.publishSubject.onNext("huobi")
                viewModel.publishSubject.onComplete()
                changeLayout("upbit")
            }
            else -> openOptionDialog()
        }
        // 기준 url인 거 다른 걸로 바꿔볼까?
        // 기존 코드에선 바뀐 url 기준으로 getData()랑 init() 새로 하는 방식이었는데
        // 어차피 하려면 뷰모델에서 해야 한단 말이지
        // 뷰모델 기준 url을 바꾸는 걸로 해야 하나?
        return true
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModel.Factory(application)).get(ViewModel::class.java)
        viewModel.onCreate()
        adapter.coinInfos = viewModel.coinInfos
        adapter.exchange = exchange
        binding.apply {
            recyclerView.adapter = adapter
            swipeRefreshLayout.setOnRefreshListener {

                // 기존엔 getData() -> init()을 해야 했다
                // 근데 이번엔 init()을 한 번만 해도 되게 만들 거란 말이야
                //
            }
        }
        viewModel.liveCoinInfos.observe(this, { coinInfos ->
            adapter.coinInfos = coinInfos
            adapter.exchange = exchange
            binding.recyclerView.adapter = adapter
        })
    }

    private fun changeLayout(exchange: String) {
        when (exchange) {
            "coinone" -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(0xFF0079FE.toInt()))
                supportActionBar!!.title = "Coinone"
            }
            "bithumb" -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(0xFFF37321.toInt()))
                supportActionBar!!.title = "Bithumb"
            }
            "upbit" -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(0xFF073686.toInt()))
                supportActionBar!!.title = "Upbit"
            }
            "huobi" -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(0xFF1C2143.toInt()))
                supportActionBar!!.title = "Huobi"
            }
            else -> TODO()
        }
    }

    private fun openOptionDialog() {

    }

    private fun println(data: String) {
        Log.d("MainAcitivity", data)
    }
}