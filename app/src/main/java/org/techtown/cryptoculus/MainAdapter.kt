package org.techtown.cryptoculus

import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.databinding.ItemCoinBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.ticker.TickerBithumb
import org.techtown.cryptoculus.ticker.TickerCoinone
import org.techtown.cryptoculus.ticker.TickerHuobi
import org.techtown.cryptoculus.ticker.TickerUpbit
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var coinInfos = ArrayList<CoinInfo>()
    var exchange = ""
    private val onItemClicked = MutableLiveData<CoinInfo>()
    val onItemMoved = MutableLiveData<ArrayList<Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCoinBinding.inflate(inflater, parent, false)

        return ViewHolder(binding,
            onItemClicked = { coinInfo ->
                onItemClicked.value = coinInfo
            }
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(coinInfos[position])
    }

    override fun getItemCount(): Int = coinInfos.size

    inner class ViewHolder(
            private val binding: ItemCoinBinding,
            private val onItemClicked: (CoinInfo) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinInfo: CoinInfo) {
            // 이건 binding만 해 주면 되는 함수야
            // 여기서 가공을 하거나 할 필요가 없는 거다
            binding.viewHolder = this
            binding.coinInfo = coinInfo

            /* val imageId =
                if (Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName) == 0) // 0 means 'cannot find'
                    R.drawable.basic
                else
                    Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName)


            binding.imageView.setImageResource(imageId) */

            binding.imageView.setImageResource(
                if (Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName) == 0) // 0 means 'cannot find'
                    R.drawable.basic
                else
                    Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName)
            )

            binding.textView.text = "${coinInfo.coinName} / ${coinInfo.coinNameKorean}"

            binding.clicked = false

            binding.executePendingBindings()
        }

        fun onClick(clicked: Boolean) {
            changeVisibility(clicked)
        }

        private fun changeVisibility(isExpanded: Boolean) {
            binding.clicked = !(binding.clicked)
            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            val valueAnimator = if (isExpanded) ValueAnimator.ofInt(0, 600) else ValueAnimator.ofInt(600, 0)
            // Animation이 실행되는 시간, n/1000초
            valueAnimator.duration = 500
            valueAnimator.addUpdateListener { animation -> // imageView의 높이 변경
                binding.linearLayout.layoutParams.height = animation.animatedValue as Int
                binding.linearLayout.requestLayout()
                // imageView가 실제로 사라지게하는 부분
                binding.linearLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }
            // Animation start
            valueAnimator.start()
        }
    }
}