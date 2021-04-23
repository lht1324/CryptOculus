package org.techtown.cryptoculus.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var callback: OnBackPressedCallback
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("onCreateView() is executed in MainFragment.")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        setHasOptionsMenu(true)

        init()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).onBackPressedMainFragment()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.option_menu, menu)

        val item = menu?.findItem(R.id.spinner)
        val spinner = item?.actionView as AppCompatSpinner
        var arrayAdapter =
                ArrayAdapter(
                        requireActivity(),
                        R.layout.item_spinner,
                        arrayOf("Coinone", "Bithumb", "Upbit", "Huobi")
                )
        spinner.dropDownWidth = ListPopupWindow.WRAP_CONTENT
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // viewModel.getData(spinner.getItemAtPosition(position).toLowerCase())
                when (spinner.getItemAtPosition(position)) {
                    "Coinone" -> useViewModel().getDataCoinone()
                    "Bithumb" -> useViewModel().getDataBithumb()
                    "Upbit" -> useViewModel().getDataUpbit()
                    "Huobi" -> useViewModel().getDataHuobi()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option)
            (activity as MainActivity).changeToOptionFragment()

        return true
    }

    private fun init() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.apply {
            recyclerView.adapter = mainAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)

            swipeRefreshLayout.setOnRefreshListener {
                useViewModel().refreshCoinInfos()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        useViewModel().getCoinInfos().observe(viewLifecycleOwner, { coinInfos ->
            (activity as MainActivity).changeLayout(coinInfos[0].exchange)
            mainAdapter.coinInfos = coinInfos
            mainAdapter.notifyDataSetChanged()
        })

        useViewModel().getNews().observe(viewLifecycleOwner, { news ->
            // 일단 다이얼로그를 띄워야지
            // 메인 리사이클러뷰 업데이트 한 다음에 띄워야 하나?
            /*
            news[0] = 상태(new, old, both)
            news[1] = 신규상장 리스트
            news[2] = 상장폐지 리스트
            news[lastIndex] = type
             */
            if (news.size == 2) {

            } else {

            }
        })
    }

    private fun useViewModel() = (activity as MainActivity).viewModel

    private fun println(data: String) = Log.d("MainFragment", data)
}