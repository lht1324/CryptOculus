package org.techtown.cryptoculus.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.ItemOptionBinding
import org.techtown.cryptoculus.repository.model.CoinInfo
import java.util.*
import kotlin.collections.ArrayList

class PreferencesAdapter : RecyclerView.Adapter<PreferencesAdapter.ViewHolder>() {
    var coinInfos = ArrayList<CoinInfo>()
    val clickedItem = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOptionBinding.inflate(inflater, parent, false)

        return ViewHolder(binding,
            onItemClicked = { coinName ->
                clickedItem.value = coinName
            }
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(coinInfos[position], position)
    }

    override fun getItemCount() = coinInfos.size

    inner class ViewHolder(private val binding: ItemOptionBinding,
        private val onItemClicked: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coinInfo: CoinInfo, position: Int) = binding.apply {
                viewHolder = this@ViewHolder
                this.coinInfo = coinInfo
                this.position = position

                val drawableId = root.resources.getIdentifier(
                        coinInfo.coinName.toLowerCase(),
                        "drawable",
                        root.context.packageName)

                imageView.setImageResource(
                        if (drawableId == 0)
                            R.drawable.basic
                        else
                            drawableId
                )

                checkedTextView.isChecked = coinInfo.coinViewCheck
            }

        fun changeCoinView(coinViewCheck: Boolean, position: Int) {
            coinInfos[position].coinViewCheck = !coinViewCheck
            binding.checkedTextView.toggle()
            onItemClicked(coinInfos[position].coinName)
            // 여기서 뷰모델 업데이트도 해 줄까?
            // onItemClicked해서 클릭된 거 업데이트 하면 되잖아
        }

        fun getCoinNameKorean(coinName: String): String {
            val id = binding.root.resources.getIdentifier(
                    coinName,
                    "string",
                    binding.root.context.packageName
            )
            println("id of $coinName = $id in getCoinNameKorean()")

            return if (id != 0)
                binding.root.resources.getString(id)
            else
                "신규 상장"
        }
    }
}