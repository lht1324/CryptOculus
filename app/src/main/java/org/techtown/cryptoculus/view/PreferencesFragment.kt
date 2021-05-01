package org.techtown.cryptoculus.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentOptionBinding

class PreferencesFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    private lateinit var callback: OnBackPressedCallback
    private val preferencesAdapter: PreferencesAdapter by lazy {
        PreferencesAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option, container, false)

        setHasOptionsMenu(true)

        init()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).backToMainActivity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == android.R.id.home)
            (activity as MainActivity).supportFragmentManager.popBackStack()

        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init() {
        preferencesAdapter.coinInfos = useViewModel().getSavedCoinInfos()

        binding.apply {
            recyclerView.apply {
                adapter = preferencesAdapter
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(RecyclerViewDecoration(30))
            }

            checkedTextView.apply {
                isChecked = useViewModel().getOptionCheckAll()

                setOnClickListener {
                    preferencesAdapter.apply {
                        coinInfos.replaceAll {
                            it.coinViewCheck = !(checkedTextView.isChecked)
                            it
                        }
                        // 여기서 이러는 게 아니라 뷰모델에서 달라진 걸 넣어줘야 하는 거 아냐?
                        // useViewModel().checkedAll() 이런 식으로
                        // 뤼이드에서 들었지
                        // 뷰모델 하나한테 모든 걸 맏기기보단 분업화하는 게 낫다고

                        notifyDataSetChanged()
                    }

                    checkedTextView.toggle()
                }
            }
        }

        preferencesAdapter.clickedItem.observe(viewLifecycleOwner, { coinInfo ->
            useViewModel().update(coinInfo)
        })
    }

    private fun useViewModel() = (activity as MainActivity).mainViewModel

    private fun println(data: String) = Log.d("OptionFragment", data)
}