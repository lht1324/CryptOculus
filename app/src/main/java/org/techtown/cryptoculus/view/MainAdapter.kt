package org.techtown.cryptoculus.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemCoinBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.viewmodel.MainViewModel
import org.techtown.cryptoculus.viewmodel.SortingViewModel
import java.util.*
import kotlin.collections.ArrayList

class MainAdapter(private val mContext: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>(), Filterable {
    var coinInfos = ArrayList<CoinInfo>()
    var filteredCoinInfos = ArrayList<CoinInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCoinBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(filteredCoinInfos[position])
    }

    override fun getItemCount() = filteredCoinInfos.size

    inner class ViewHolder(
        private val binding: ItemCoinBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinInfo: CoinInfo) = binding.apply {
            viewHolder = this@ViewHolder
            this.coinInfo = coinInfo

            val drawableId = root.resources.getIdentifier(
                    coinInfo.coinName.toLowerCase(),
                    "drawable",
                    root.context.packageName)

            imageView.setImageResource(
                    if (drawableId == 0) R.drawable.basic
                    else drawableId
            )

            clicked = true

            textView3.setTextColor(when {
                coinInfo.ticker.changeRate.toDouble() < 0.0 -> Color.rgb(0, 0, 255)
                coinInfo.ticker.changeRate.toDouble() > 0.0 -> Color.rgb(255, 0, 0)
                else -> Color.rgb(128, 128, 128)
            })
        }

        fun onClick(clicked: Boolean) = changeVisibility(clicked)

        fun getCoinNameKorean(coinName: String): String {
            val id = binding.root.resources.getIdentifier(
                coinName,
                "string",
                "org.techtown.cryptoculus"
            )

            return if (id != 0)
                    binding.root.resources.getString(id)
                else
                    "신규 상장"
        }

        private fun changeVisibility(isExpanded: Boolean) {
            binding.clicked = !(binding.clicked!!)
            binding.linearLayout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            val height = binding.linearLayout.measuredHeight
            val valueAnimator = if (isExpanded)
                ValueAnimator.ofInt(0, height)
            else
                ValueAnimator.ofInt(height, 0)
            // Animation이 실행되는 시간, n/1000초
            valueAnimator.duration = 500
            valueAnimator.addUpdateListener { animation -> // imageView의 높이 변경
                binding.linearLayout.apply {
                    layoutParams.height = animation.animatedValue as Int
                    requestLayout()
                    visibility = if (isExpanded) View.VISIBLE else View.GONE
                }
            }
            // 터치했을 때 바로 사라지는 게 아니라 부드럽게 접히는 걸로 해 볼까?
            // 방향만 반대로 하면 되는 거 아냐

            valueAnimator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.linearLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            })
            // Animation start
            valueAnimator.start()
        }
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val charString = constraint.toString()

            filteredCoinInfos = if (charString.isEmpty())
                coinInfos
            else {
                val filteredList = ArrayList<CoinInfo>()

                if (coinInfos.isNotEmpty()) {
                    for (i in coinInfos.indices) {
                        val id = mContext.resources.getIdentifier(
                            coinInfos[i].coinName,
                            "string",
                            "org.techtown.cryptoculus"
                        )
                        val coinNameKorean = if (id != 0) mContext.resources.getString(id)
                        else "신규 상장"

                        if("${coinInfos[i].coinName} / $coinNameKorean".toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(coinInfos[i])
                    }
                }
                filteredList
            }
            val filterResults = FilterResults()
            filterResults.values = filteredCoinInfos
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredCoinInfos  = results.values as ArrayList<CoinInfo>
            notifyDataSetChanged()
        }
    }

    fun setItems(coinInfos: ArrayList<CoinInfo>) {
        filteredCoinInfos = coinInfos
        this.coinInfos = coinInfos
    }

    private fun println(data: String) = Log.d("MainAdapter", data)
}