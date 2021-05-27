package org.techtown.cryptoculus.view

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemCoinBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*
import kotlin.collections.ArrayList

class MainAdapter(private val mContext: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>(), Filterable {
    var coinInfos = ArrayList<CoinInfo>()
    var filteredCoinInfos = ArrayList<CoinInfo>()
    var openChart = MutableLiveData<String>()
    var clickedItem = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCoinBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(filteredCoinInfos[position])
    }

    override fun getItemCount() = filteredCoinInfos.size

    override fun getItemId(position: Int) = filteredCoinInfos[position].exchange.hashCode().toLong() + filteredCoinInfos[position].coinName.hashCode().toLong()

    inner class ViewHolder(
        private val binding: ItemCoinBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinInfo: CoinInfo) = binding.apply {
            this.coinInfo = coinInfo
            coinNameKorean = getCoinNameKorean(coinInfo.coinName)

            val drawableId = root.resources.getIdentifier(
                when (coinInfo.coinName) {
                    "1INCH" -> "inch"
                    "CON" -> "conun"
                    "TRUE" -> "truechain"
                    else -> coinInfo.coinName.toLowerCase()
                },
                "drawable",
                root.context.packageName)

            imageView.setImageResource(
                    if (drawableId == 0) R.drawable.default_image
                    else drawableId
            )

            textView3.setTextColor(when {
                coinInfo.ticker.changeRate.toDouble() < 0.0 -> Color.rgb(0, 0, 255)
                coinInfo.ticker.changeRate.toDouble() > 0.0 -> Color.rgb(255, 0, 0)
                else -> Color.rgb(128, 128, 128)
            })

            // 뷰홀더의 linearLayout2.visibility가 변경되는 만큼 뷰홀더 자체를 재활용 하면 visibility는 그대로 유지된다
            // clicked는 3초에 1번씩 업데이트 될 때마다 계속 false가 된다
            linearLayout2.visibility = if (coinInfo.clicked) View.VISIBLE else View.GONE

            linearLayout1.clicks()
                .subscribe {
                    coinInfo.clicked = itemFolding(!coinInfo.clicked, linearLayout2)
                    clickedItem.value = coinInfo.coinName
                }

            linearLayout2.clicks()
                .subscribe {
                    showDialog(this.coinInfo!!.coinName)
                }
        }

        private fun itemFolding(clicked: Boolean, layoutExpand: LinearLayout): Boolean {
            if (clicked)
                ToggleAnimation.expand(layoutExpand)
            else
                ToggleAnimation.collapse(layoutExpand)
            return clicked
        }

        private fun getCoinNameKorean(coinName: String): String {
            val id = binding.root.resources.getIdentifier(
                if (coinName != "1INCH") coinName else "INCH",
                "string",
                "org.techtown.cryptoculus"
            )

            return if (id != 0)
                    binding.root.resources.getString(id)
                else
                    "신규 상장"
        }

        private fun showDialog(coinName: String): AlertDialog = AlertDialog.Builder(mContext)
                .setTitle("예를 누르면 거래소 차트 페이지로 이동해요.")
                .setMessage("이동하시겠어요?")
                .setPositiveButton("네.") { _: DialogInterface, _: Int ->
                    openChart.value = coinName
                }
                .setNegativeButton("아니요.") { _: DialogInterface, _: Int ->
                }
                .show()
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
                            if (coinInfos[i].coinName != "1INCH") coinInfos[i].coinName else "INCH",
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
            filteredCoinInfos = results.values as ArrayList<CoinInfo>
            notifyDataSetChanged()
        }
    }

    fun setItems(coinInfos: ArrayList<CoinInfo>) {
        if (filteredCoinInfos.size != this.coinInfos.size) {
            for (i in filteredCoinInfos.indices)
                filteredCoinInfos[i] = coinInfos[coinInfos.indexOf(filteredCoinInfos[i])]
        }
        else
            filteredCoinInfos = coinInfos
        this.coinInfos = coinInfos
        notifyDataSetChanged()
    }

    private fun println(data: String) = Log.d("MainAdapter", data)
}