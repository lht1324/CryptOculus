package org.techtown.cryptoculus.view

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

@RequiresApi(Build.VERSION_CODES.N)
class PreferencesFragment(private val application: Application) : Fragment() {
    private lateinit var binding: FragmentPreferencesBinding
    private lateinit var viewModel: PreferencesViewModel
    private lateinit var callback: OnBackPressedCallback
    private val preferencesAdapter: PreferencesAdapter by lazy {
        PreferencesAdapter(requireContext())
    }

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

    private fun init() {
        viewModel = ViewModelProvider(this, PreferencesViewModel.Factory(application)).get(PreferencesViewModel::class.java)
        preferencesAdapter.setHasStableIds(true)

        binding.apply {
            recyclerView.apply {
                adapter = preferencesAdapter
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(RecyclerViewDecoration(30))
            }

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (preferencesAdapter.coinInfos.isNotEmpty()) // 이 조건을 안 넣으면 비동기 처리 중이라서 어댑터 내의 Array가 비었을 때도 텍스트 변경을 관찰한다.
                        preferencesAdapter.filter.filter(charSequence)
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            checkedTextView.apply {
                viewModel.getCheckAll().observe(viewLifecycleOwner, { // 이게 문제 아냐?
                    isChecked = it
                })

                setOnClickListener {
                    if (editText.text.isNotBlank())
                        editText.text.clear()
                    preferencesAdapter.setItems(viewModel.changeAllChecks(preferencesAdapter.coinInfos, isChecked))
                }
            }
        }

        viewModel.getCoinInfos().observe(viewLifecycleOwner, {
            preferencesAdapter.setItems(it)
        })

        preferencesAdapter.clickedItem.observe(viewLifecycleOwner, { coinName ->
            viewModel.updateCoinViewCheck(coinName)
        })
    }

    private fun println(data: String) = Log.d("PreferencesFragment", data)
}