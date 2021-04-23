package org.techtown.cryptoculus.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// 리사이클러뷰 아이템 간의 간격을 설정
class RecyclerViewDecoration(private val divWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = divWidth
    }
}