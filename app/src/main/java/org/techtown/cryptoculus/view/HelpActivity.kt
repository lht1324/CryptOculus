package org.techtown.cryptoculus.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ActivityHelpBinding

// 도움말 화면 액티비티
class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    private val pagerAdapter by lazy {
        PagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_help)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)

        init()
    }

    override fun onBackPressed() = finish()

    private fun init() {
        // 액션 바 숨기기
        supportActionBar!!.hide()

        // 프래그먼트 생성
        pagerAdapter.fragments.apply {
            add(HelpFragment("화폐를 터치하면 시세 정보를 볼 수 있어요.", R.drawable.help1))
            add(HelpFragment("시세 정보를 터치하면 거래소의 차트 페이지가 열려요.", R.drawable.help2))
            add(HelpFragment("화폐 정렬 방식을 옵션에서 바꿀 수 있어요.", R.drawable.help3))
            add(HelpFragment("원하는 화폐만 보고 싶다면 옵션에서 고를 수 있어요.", R.drawable.help4))
        }

        binding.apply {
            viewPager.apply {
                // 페이지 변경 시 페이지 수도 변경
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        textView.text = (currentItem + 1).toString()
                    }
                })

                adapter = pagerAdapter
            }

            // 완료 버튼 ( ✓ )
            buttonSkip.clicks().subscribe { finish() }

            // 다음 버튼 ( > )
            buttonNext.clicks().subscribe {
                viewPager.apply {
                    if (currentItem == pagerAdapter.fragments.size - 1)
                        finish()
                    
                    if (currentItem < pagerAdapter.fragments.size)
                        setCurrentItem(currentItem + 1, true)
                }
            }

            // 전체 페이지 수
            textView2.text = pagerAdapter.fragments.size.toString()
        }
    }

    // 로그 출력
    private fun println(data: String) = Log.d("HelpActivity", data)
}