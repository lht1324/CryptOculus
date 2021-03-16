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
    var clicked = false
    private val onItemClicked = MutableLiveData<CoinInfo>()
    val onItemMoved = MutableLiveData<ArrayList<Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCoinBinding.inflate(inflater, parent, false)

        binding.imageView.setImageResource(R.drawable.abl)
        // 시가, 종가, 고가, 저가, 거래량, 전일대비, 거래대금

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
            binding.viewHolder = this
            // imageResource가 null일 경우를 대비해야 한다
            val imageId =
                if (Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName) == 0) // 0 means 'cannot find'
                    R.drawable.basic
                else
                    Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName)




            binding.imageView.setImageResource(imageId)
            coinInfo.coinNameKorean =
                    if (Resources.getSystem().getString(Resources.getSystem().getIdentifier(coinInfo.coinName, "strings", binding.root.context.packageName))
            binding.coinInfo = coinInfo

            binding.executePendingBindings()
        }

        fun onClick(coinInfo: CoinInfo) {
            onItemClicked(coinInfo)

            when (exchange) {
                "coinone" -> {
                    val ticker = coinInfo.ticker!! as TickerCoinone
                    val formatter = if (ticker.last.toDouble() - ticker.last.toDouble().roundToInt() != 0.0) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    binding.coinInfo.ticker!!.firstInTicker = formatter.format(ticker.first.toDouble())
                    binding.coinInfo.ticker!!.lastInTicker = formatter.format(ticker.last.toDouble())
                    binding.coinInfo.ticker!!.highInTicker = formatter.format(ticker.high.toDouble())
                    binding.coinInfo.ticker!!.lowInTicker = formatter.format(ticker.low.toDouble())
                    binding.coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.volume.toDouble())
                    // binding.coinInfo.ticker.tradeValueInTicker = formatter.format(ticker.)
                    // 그냥 거래량 같은 걸 추가하는 게 낫지 않아?
                }
                "bithumb" -> {
                    val ticker = coinInfo.ticker!! as TickerBithumb
                    val formatter = if (ticker.closingPrice.toDouble() - ticker.closingPrice.toDouble().roundToInt() != 0.0) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    binding.coinInfo.ticker!!.firstInTicker = formatter.format(ticker.openingPrice.toDouble())
                    binding.coinInfo.ticker!!.lastInTicker = formatter.format(ticker.closingPrice.toDouble())
                    binding.coinInfo.ticker!!.highInTicker = formatter.format(ticker.maxPrice.toDouble())
                    binding.coinInfo.ticker!!.lowInTicker = formatter.format(ticker.minPrice.toDouble())
                    binding.coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.unitsTraded.toDouble())
                }
                "upbit" -> {
                    val ticker = coinInfo.ticker!! as TickerUpbit
                    val formatter = if (ticker.tradePrice.toDouble() - ticker.tradePrice.toDouble().roundToInt() != 0.0) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    binding.coinInfo.ticker!!.firstInTicker = formatter.format(ticker.openingPrice.toDouble())
                    binding.coinInfo.ticker!!.lastInTicker = formatter.format(ticker.tradePrice.toDouble())
                    binding.coinInfo.ticker!!.highInTicker = formatter.format(ticker.highPrice.toDouble())
                    binding.coinInfo.ticker!!.lowInTicker = formatter.format(ticker.lowPrice.toDouble())
                    binding.coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.tradeVolume.toDouble())
                }
                "huobi" -> {
                    val ticker = coinInfo.ticker!! as TickerHuobi
                    val formatter = if (ticker.close.toDouble() - ticker.close.toDouble().roundToInt() != 0.0) DecimalFormat("###,##0.00") else DecimalFormat("###,###")
                    binding.coinInfo.ticker!!.firstInTicker = formatter.format(ticker.open.toDouble())
                    binding.coinInfo.ticker!!.lastInTicker = formatter.format(ticker.close.toDouble())
                    binding.coinInfo.ticker!!.highInTicker = formatter.format(ticker.high.toDouble())
                    binding.coinInfo.ticker!!.lowInTicker = formatter.format(ticker.low.toDouble())
                    binding.coinInfo.ticker!!.volumeInTicker = formatter.format(ticker.vol.toDouble())
                }
            }
            val context = binding.root.context
            changeVisibility(clicked)
        }

        private fun changeVisibility(isExpanded: Boolean) {
            clicked = !clicked
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