package org.techtown.cryptoculus

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.databinding.ItemOptionBinding
import org.techtown.cryptoculus.repository.model.CoinInfo

class OptionAdapter : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    var coinInfos = ArrayList<CoinInfo>()
    // coinViewCheck, position?
    // position -> coinInfos[position].coinViewCheck = !coinInfos[position.coinViewCheck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOptionBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(coinInfos[position])
    }

    override fun getItemCount(): Int = coinInfos.size

    inner class ViewHolder(private val binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coinInfo: CoinInfo) {
            binding.coinInfo = coinInfo

            binding.imageView.setImageResource(
                    if (Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName) == 0) // 0 means 'cannot find'
                        R.drawable.basic
                    else
                        Resources.getSystem().getIdentifier(coinInfo.coinName, "drawable", binding.root.context.packageName)
            )
        }

        fun changeCoinView(coinInfo: CoinInfo, position: Int) {
            coinInfos[position].coinViewCheck = !(coinInfo.coinViewCheck)
            // 이건 자동으로 바뀌게 해야 하는 거 아냐?
            binding.coinInfo.coinViewCheck = !(binding.coinInfo.coinViewCheck)
        }
    }
}