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
            // imageResource가 null일 경우를 대비해야 한다
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
            /* binding.coinInfo.coinNameKorean =
                if (Resources.getSystem().getString(Resources.getSystem().getIdentifier(coinInfo.coinName, "strings", binding.root.context.packageName)).isEmpty())
                    "신규 상장"
                else
                    Resources.getSystem().getString(Resources.getSystem().getIdentifier(coinInfo.coinName, "strings", binding.root.context.packageName))
            */
            // coinNameKorean도 밖에서 해 줘야 하는 거 아냐?
            // 근데 뷰모델에서 어떻게 액세스하냐
            // 상장 폐지도 추가해야 한다
            // 상장 폐지는 api에 없는데 여기에 있는 경우지
            // 일단 여기가 아니라 다른 곳에 달아야 하나

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