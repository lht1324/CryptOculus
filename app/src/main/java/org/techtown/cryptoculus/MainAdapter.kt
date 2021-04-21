package org.techtown.cryptoculus

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.databinding.ItemCoinBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*
import kotlin.collections.ArrayList


class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var coinInfos = ArrayList<CoinInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCoinBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(coinInfos[position])
    }

    override fun getItemCount(): Int = coinInfos.size

    inner class ViewHolder(
        private val binding: ItemCoinBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinInfo: CoinInfo) {
            binding.viewHolder = this
            binding.coinInfo = coinInfo

            binding.imageView.setImageResource(
                if (binding.root.resources.getIdentifier(
                        coinInfo.coinName.toLowerCase(Locale.ROOT),
                        "drawable",
                        binding.root.context.packageName) == 0
                ) // 0 means 'cannot find'
                    R.drawable.basic
                else
                    binding.root.resources.getIdentifier(
                        coinInfo.coinName.toLowerCase(Locale.ROOT),
                        "drawable",
                        binding.root.context.packageName)
            )

            binding.clicked = true

            binding.executePendingBindings()
        }

        fun onClick(clicked: Boolean, coinInfo: CoinInfo) {
            changeVisibility(clicked)
        }

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
                binding.linearLayout.layoutParams.height = animation.animatedValue as Int
                binding.linearLayout.requestLayout()
                // imageView가 실제로 사라지게하는 부분
                binding.linearLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
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

    fun println(data: String) = Log.d("MainAdapter", data)
}