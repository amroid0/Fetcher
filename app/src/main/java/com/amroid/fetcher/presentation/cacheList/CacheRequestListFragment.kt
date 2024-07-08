package com.amroid.fetcher.presentation.cacheList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.amroid.fetcher.R
import com.amroid.fetcher.databinding.FragmentCacheListBinding
import com.amroid.fetcher.domain.entities.SortType
import com.amroid.fetcher.presentation.SharedViewModel


class CacheRequestListFragment : Fragment() {
  private var _binding: FragmentCacheListBinding? = null
  private val adapter: CacheListAdapter by lazy {
    CacheListAdapter(onClick = {
      sharedViewModel.naviagteToDetail(it)
    })
  }
  private val viewModel by lazy {
    ViewModelProvider(this, CacheListViewModel.Factory)[CacheListViewModel::class.java]
  }
  private val sharedViewModel by lazy {
    ViewModelProvider(requireActivity())[SharedViewModel::class.java]
  }
  private val binding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentCacheListBinding.inflate(inflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
    setupListeners()
    viewModel.getCachedRequestList(SortType.TIME)
  }

  private fun setupListeners() {
    binding.sortGroup.setOnCheckedChangeListener { radioGroup, id ->
      var sortType: SortType = SortType.TIME
      when (id) {
        R.id.time_radio -> {
          sortType = SortType.TIME
        }

        R.id.status_radio -> {
          sortType = SortType.RESPONSE_STATUS

        }

        R.id.type_radio -> {
          sortType = SortType.REQUEST_TYPE
        }
      }
      viewModel.getCachedRequestList(sortType)
    }
    viewModel.getCacheListState().observe(viewLifecycleOwner) {
      binding.progress.visibility = View.GONE
      when(it){
        CacheListState.Loading -> {
          binding.requestRecycleView.visibility = View.GONE
          binding.progress.visibility = View.VISIBLE
        }
        is CacheListState.OnError -> {
          binding.requestRecycleView.visibility = View.VISIBLE
          Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
        is CacheListState.Success -> {
          binding.requestRecycleView.visibility = View.VISIBLE
          adapter.addData(it.responses)
        }
      }

    }
  }


  private fun initViews() {
    binding.requestRecycleView.adapter = adapter
  }
}