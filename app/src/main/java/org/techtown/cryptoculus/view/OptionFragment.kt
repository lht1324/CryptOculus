package org.techtown.cryptoculus.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentOptionBinding

class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    private lateinit var callback: OnBackPressedCallback
    private val optionAdapter: OptionAdapter by lazy {
        OptionAdapter()
    }

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

    private fun init() {
        optionAdapter.coinInfos = useViewModel().getSavedCoinInfos()

        binding.recyclerView.apply {
            adapter = optionAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(RecyclerViewDecoration(30))
        }

        optionAdapter.clickedItem.observe(viewLifecycleOwner, { coinInfo ->
            useViewModel().update(coinInfo)
        })
    }

    private fun useViewModel() = (activity as MainActivity).viewModel

    private fun println(data: String) = Log.d("OptionFragment", data)
}