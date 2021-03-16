package org.techtown.cryptoculus.function

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags: Int = ItemTouchHelper.UP  or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition())
        return true
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true;
    }
}