package org.techtown.cryptoculus

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.viewmodel.ViewModel

// CryptOculusMVVM without Database Temporary

class MainActivity : AppCompatActivity() {
    // 고칠 것
    // 옵션 액티비티가 아니라 다이얼로그로 띄워보자
    // Rx 사용
    // 터치하기 전엔 간단한 정보만 보여주다가 터치하면 상세 정보
    // 터치한 곳에 버튼 넣어서 거래소 차트로 연결해주기
    // 검색
    // 한 번 DB를 빼고 만들어 보자
    // 받아오는 기능만 만든 뒤 그게 완전해지면 추가하는 게 나을 것 같다
    
    // coinInfos 변경되는 경우
    // 메뉴 클릭
    // swipeRefreshLayout 작동
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private val mainAdapter by lazy {
        MainAdapter()
    }
    private var backPressedLast: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Retrofit Call execute()
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

    override fun onStart() {
        super.onStart()

        println("onstart")
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
        if (!getSharedPreferences("restartApp", MODE_PRIVATE).getBoolean("restartApp", false))
            getSharedPreferences("restartApp", MODE_PRIVATE)
                .edit()
                .putBoolean("restartApp", true)
                .apply()

        getSharedPreferences("exchange", MODE_PRIVATE)
                .edit()
                .putString("exchange", viewModel.getExchange())
                .apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.coinone ->
                viewModel.getDataCoinone()

            R.id.bithumb ->
                viewModel.getDataBithumb()

            R.id.upbit ->
                viewModel.getDataUpbit()

            R.id.huobi ->
                viewModel.getDataHuobi()

            else -> openOptionDialog(mainAdapter.coinInfos)
        }

        return true
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModel.Factory(
                getSharedPreferences("exchange", MODE_PRIVATE)
                        .getString("exchange", "coinone")!!,
                getSharedPreferences("restartApp", MODE_PRIVATE)
                        .getBoolean("restartApp", false),
                application
        )).get(ViewModel::class.java)

        binding.apply {
            recyclerView.adapter = mainAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshCoinInfos()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.getCoinInfos().observe(this, { coinInfos ->
            changeLayout(coinInfos[0].exchange)
            mainAdapter.coinInfos = coinInfos
            mainAdapter.notifyDataSetChanged()
        })
        // 뷰모델의 coinInfos가 변경되는 조건
        // 1. 액티비티에서 메뉴가 바뀐다
        // 2. 일정 시간마다 업데이트되는 기능을 만들 건데 업데이트 될 때도 바뀐다
        viewModel.getNews().observe(this, { news ->
            // 일단 다이얼로그를 띄워야지
            // 메인 리사이클러뷰 업데이트 한 다음에 띄워야 하나?
            /*
            news[0] = 상태(new, old, both)
            news[1] = 신규상장 리스트
            news[2] = 상장폐지 리스트
            news[lastIndex] = type
             */
            if (news.size == 2) {
                
            }
            else  {

            }
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
        }
    }

    private fun openOptionDialog(coinInfos: ArrayList<CoinInfo>) {
        val optionDialog = OptionDialog(this)
        optionDialog.optionAdapter.coinInfos = coinInfos
        optionDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        optionDialog.setOnDismissListener {
            viewModel.updateCoinInfos(optionDialog.optionAdapter.coinInfos)
        }
        // 여기서 DB 추가해야 하나?

        optionDialog.setCancelable(true)
        optionDialog.show()
    }

    private fun println(data: String) {
        Log.d("MainAcitivity", data)
    }
}