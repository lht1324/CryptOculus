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
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemCoinBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
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

    inner class ViewHolder(private val binding: ItemCoinBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coinInfo: CoinInfo) = binding.apply {
            this.coinInfo = coinInfo
            changeRate = getChangeRate(coinInfo.ticker.changeRate)

            Glide.with(root)
                .load(coinInfo.image)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageView)

            textView3.setTextColor(
                if (coinInfo.ticker.changeRate!= "∞") { // 당일 상장되어 전일종가가 없을 때 오류 발생
                    when {
                        coinInfo.ticker.changeRate.toDouble() < 0.0 -> Color.rgb(0, 0, 255)
                        coinInfo.ticker.changeRate.toDouble() > 0.0 -> Color.rgb(255, 0, 0)
                        else -> Color.rgb(128, 128, 128)
                    }
                }
                else
                    Color.rgb(110, 202, 243)
            )

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

        private fun getChangeRate(originalChangeRate: String) = if (originalChangeRate != "∞")
            "$originalChangeRate%"
        else
            "당일 상장"

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
            val filteredList = ArrayList<CoinInfo>()

            if (charString.isEmpty())
                filteredList.addAll(coinInfos)

            else {
                if (coinInfos.isNotEmpty()) {
                    for (i in coinInfos.indices) {
                        if("${coinInfos[i].coinName} / $${coinInfos[i].coinNameKorean}".lowercase().contains(charString.lowercase()))
                            filteredList.add(coinInfos[i])
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredCoinInfos = ArrayList()
            filteredCoinInfos.addAll(results.values as ArrayList<CoinInfo>)
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