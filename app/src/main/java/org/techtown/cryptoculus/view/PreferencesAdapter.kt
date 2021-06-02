package org.techtown.cryptoculus.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemPreferencesBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*
import kotlin.collections.ArrayList

class PreferencesAdapter(private val mContext: Context) : RecyclerView.Adapter<PreferencesAdapter.ViewHolder>(), Filterable {
    var coinInfos = ArrayList<CoinInfo>()
    var filteredCoinInfos = ArrayList<CoinInfo>()
    val clickedItem = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPreferencesBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(filteredCoinInfos[position])
    }

    override fun getItemCount() = filteredCoinInfos.size

    override fun getItemId(position: Int) = filteredCoinInfos[position].exchange.hashCode().toLong() + filteredCoinInfos[position].coinName.hashCode().toLong()

    inner class ViewHolder(private val binding: ItemPreferencesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coinInfo: CoinInfo) = binding.apply {
                viewHolder = this@ViewHolder
                this.coinInfo = coinInfo

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
                        if (drawableId == 0)
                            R.drawable.default_image
                        else
                            drawableId
                )

                checkedTextView.clicks().subscribe {
                    clickedItem.value = coinInfo.coinName
                    coinInfos[coinInfos.indexOf(coinInfo)].coinViewCheck = !coinInfos[coinInfos.indexOf(coinInfo)].coinViewCheck
                    checkedTextView.toggle()
                }
            }

        fun getCoinNameKorean(coinName: String): String {
            val id = binding.root.resources.getIdentifier(
                if (coinName != "1INCH") coinName else "INCH",
                "string",
                binding.root.context.packageName
            )

            return if (id != 0)
                binding.root.resources.getString(id)
            else
                "신규 상장"
        }
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
        this.coinInfos = coinInfos
        filteredCoinInfos = coinInfos
        notifyDataSetChanged()
    }

    private fun println(data: String) = Log.d("PreferencesAdapter", data)
}