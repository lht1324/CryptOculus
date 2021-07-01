package org.techtown.cryptoculus.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemChoiceBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import kotlin.collections.ArrayList

class ChoiceAdapter : RecyclerView.Adapter<ChoiceAdapter.ViewHolder>(), Filterable {
    var coinInfos = ArrayList<CoinInfo>()
    var filteredCoinInfos = ArrayList<CoinInfo>()
    val clickedItem = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemChoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(filteredCoinInfos[position])
    }

    override fun getItemCount() = filteredCoinInfos.size

    override fun getItemId(position: Int) = filteredCoinInfos[position].exchange.hashCode().toLong() + filteredCoinInfos[position].coinName.hashCode().toLong()

    inner class ViewHolder(private val binding: ItemChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coinInfo: CoinInfo) = binding.apply {
            this.coinInfo = coinInfo

            Glide.with(root)
                .load(coinInfo.image)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageView)

            checkedTextView.clicks().subscribe {
                clickedItem.value = coinInfo.coinName
                coinInfos[coinInfos.indexOf(coinInfo)].coinViewCheck = !coinInfos[coinInfos.indexOf(coinInfo)].coinViewCheck
                checkedTextView.toggle()
            }
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
                        if("${coinInfos[i].coinName} / ${coinInfos[i].coinNameKorean}".toLowerCase().contains(charString.toLowerCase()))
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
        this.coinInfos = coinInfos
        filteredCoinInfos = coinInfos
        notifyDataSetChanged()
    }

    private fun println(data: String) = Log.d("PreferencesAdapter", data)
}