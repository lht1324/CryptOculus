package org.techtown.cryptoculus.view

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// 도움말 화면의 뷰페이저에 사용되는 어댑터
class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    var fragments = ArrayList<HelpFragment>()

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}