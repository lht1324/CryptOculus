package org.techtown.cryptoculus.view

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentChoiceBinding
import org.techtown.cryptoculus.viewmodel.ChoiceViewModel

class ChoiceFragment(private val application: Application) : Fragment() {
    private lateinit var binding: FragmentChoiceBinding
    private lateinit var viewModel: ChoiceViewModel
    private lateinit var callback: OnBackPressedCallback
    private val choiceAdapter: ChoiceAdapter by lazy {
        ChoiceAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice, container, false)

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
            (activity as MainActivity).backToMainActivity()

        return true
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ChoiceViewModel.Factory(application)).get(ChoiceViewModel::class.java)
        choiceAdapter.setHasStableIds(true)

        binding.apply {
            recyclerView.apply {
                adapter = choiceAdapter
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(RecyclerViewDecoration(30))
            }

            editText.apply {
                textChanges().subscribe {
                    if (choiceAdapter.coinInfos.isNotEmpty())
                        choiceAdapter.filter.filter(it)
                }

                setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_ENTER }
            }

            checkedTextView.apply {
                viewModel.getCheckAll().observe(viewLifecycleOwner, { // 이게 문제 아냐?
                    isChecked = it
                })

                clicks().subscribe {
                    if (editText.text.isNotBlank())
                        editText.text.clear()
                    choiceAdapter.setItems(viewModel.changeCoinViewCheckAll(choiceAdapter.coinInfos, isChecked))
                }
            }
        }

        viewModel.getCoinInfos().observe(viewLifecycleOwner, {
            choiceAdapter.setItems(it)
        })

        choiceAdapter.clickedItem.observe(viewLifecycleOwner, { coinName ->
            viewModel.updateCoinViewCheck(coinName)
        })
    }

    private fun println(data: String) = Log.d("PreferencesFragment", data)
}