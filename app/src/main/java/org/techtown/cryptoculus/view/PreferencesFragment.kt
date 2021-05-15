package org.techtown.cryptoculus.view

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentPreferencesBinding
import org.techtown.cryptoculus.viewmodel.PreferencesViewModel

class PreferencesFragment(private val application: Application) : Fragment() {
    private lateinit var binding: FragmentPreferencesBinding
    private lateinit var viewModel: PreferencesViewModel
    private lateinit var callback: OnBackPressedCallback
    private val preferencesAdapter: PreferencesAdapter by lazy {
        PreferencesAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)

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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init() {
        viewModel = ViewModelProvider(this, PreferencesViewModel.Factory(application)).get(PreferencesViewModel::class.java)

        binding.apply {
            recyclerView.apply {
                adapter = preferencesAdapter
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(RecyclerViewDecoration(30))
            }

            checkedTextView.apply {
                viewModel.getCheckAll().observe(viewLifecycleOwner, {
                    isChecked = it
                })

                setOnClickListener {
                    preferencesAdapter.coinInfos = viewModel.changeAllChecks(preferencesAdapter.coinInfos, checkedTextView.isChecked)
                    preferencesAdapter.notifyDataSetChanged()
                }
            }
        }

        // 전체 선택이 바뀌는 경우
        // 내가 누른다
        // 전체가 채워지거나 한 개 이상이 빠진다

        viewModel.getCoinInfos().observe(viewLifecycleOwner, {
            preferencesAdapter.apply {
                coinInfos = it
                notifyDataSetChanged()
            }
        })

        preferencesAdapter.clickedItem.observe(viewLifecycleOwner, { coinName ->
            viewModel.updateCoinViewCheck(coinName)
        })
    }

    private fun println(data: String) = Log.d("PreferencesFragment", data)
}