package org.techtown.cryptoculus

import android.graphics.Color
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
    
    // coinInfos 변경되는 경우
    // 메뉴 클릭
    // swipeRefreshLayout 작동
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
            finish()
            return
        }

        Toast.makeText(this, "종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
        backPressedLast = System.currentTimeMillis()
    }

    override fun onStop() {
        super.onStop()

        // 처음 시작했을 때만 restartApp을 삽입
        // 재시작했을 땐 restartApp을 삽입하지 않는다
        // 처음 시작: true, 재시작: false
        if (getSharedPreferences("restartApp", MODE_PRIVATE).getBoolean("restartApp", false)) {
            getSharedPreferences("restartApp", MODE_PRIVATE)
                .edit()
                .putBoolean("restartApp", true)
                .apply()
        }

        // viewModel.coinDao.updateAll(viewModel.coinInfos)
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
                changeLayout("huobi")
            }
            else -> openOptionDialog()
        }
        // exchange를 바꿔줬으니까 coinInfos를 불러와야지
        // 어떤 걸로 불러오든 액티비티는 띄우기만 하면 되는 거야
        // 여기서 어댑터 재설정하든가 해라
        return true
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModel.Factory(application)).get(ViewModel::class.java)
        viewModel.onCreate()
        viewModel.restartApp = getSharedPreferences("restartApp", MODE_PRIVATE)
                .getBoolean("restartApp", false)

        adapter.coinInfos = viewModel.getCoinInfos().value!!
        binding.apply {
            recyclerView.adapter = adapter
            swipeRefreshLayout.setOnRefreshListener {
            }
        }
        viewModel.getCoinInfos().observe(this, { coinInfos ->
            adapter.coinInfos = coinInfos
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
        val optionDialog = OptionDialog(this)
        optionDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        optionDialog.setOnDismissListener {
            viewModel.updateAll(optionDialog.optionAdapter.coinInfos) { finish() }
        }

        optionDialog.setCancelable(true)
        optionDialog.show()
    }

    private fun println(data: String) {
        Log.d("MainAcitivity", data)
    }
}