package org.techtown.cryptoculus.view

import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityMainBinding
import org.techtown.cryptoculus.viewmodel.ViewModel

// CryptOculusMVVM without Database Temporary

class MainActivity : AppCompatActivity() {
    // 고칠 것
    // 터치한 곳에 버튼 넣어서 거래소 차트로 연결해주기
    // 검색
    private lateinit var binding: ActivityMainBinding
    private val mainFragment by lazy {
        MainFragment()
    }
    private var backPressedLast: Long = 0
    lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate() is executed in MainActivity")
        // if (savedInstanceState != null) 이거 붙이고 하면 일단 1번 호출된다
        // onCreate() 2번 호출되는 원인 중 하나로 추정된다
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

        if (savedInstanceState == null)
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.constraintLayout, mainFragment)
                    .commit()

        init()
    }

    fun onBackPressedMainFragment() {
        // Exit if touch once more before 2 secs
        // 2초 전에 누르면 종료
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            finish()
            return
        }

        showToast("종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.")
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

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModel.Factory(
                getSharedPreferences("exchange", MODE_PRIVATE)
                        .getString("exchange", "coinone")!!,
                getSharedPreferences("restartApp", MODE_PRIVATE)
                        .getBoolean("restartApp", false),
                application
        )).get(ViewModel::class.java)
    }

    fun changeLayout(exchange: String) = supportActionBar!!
            .setBackgroundDrawable(
                    ColorDrawable(
                            when (exchange) {
                                "coinone" -> 0xFF0079FE.toInt()
                                "bithumb" -> 0xFFF37321.toInt()
                                "upbit" -> 0xFF073686.toInt()
                                else -> 0xFF1C2143.toInt() // huobi
                            }
                    )
            )

    fun changeToOptionFragment() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.constraintLayout, OptionFragment())
                .commit()
    }

    fun changeToMainFragment() {
        // ...얘네 둘은 onCreateView() 한 번만 뜨는데?
        // add가 문제네
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        supportFragmentManager.popBackStack()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.constraintLayout, mainFragment)
                .commit()
    }

    private fun println(data: String) = Log.d("MainAcitivity", data)

    private fun showToast(data: String) = Toast
            .makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
}