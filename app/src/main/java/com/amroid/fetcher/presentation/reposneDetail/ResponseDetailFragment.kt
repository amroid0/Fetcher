package com.amroid.fetcher.presentation.reposneDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amroid.fetcher.databinding.FragmentResponseDetailBinding
import com.amroid.fetcher.presentation.SharedViewModel

class ResponseDetailFragment : Fragment() {

  private var _binding: FragmentResponseDetailBinding? = null
  private val adapter: HeaderAdapter by lazy {
    HeaderAdapter()
  }
  private val sharedViewModel by lazy {
    ViewModelProvider(requireActivity())[SharedViewModel::class.java]
  }
  private val binding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentResponseDetailBinding.inflate(inflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
  }

  private fun initViews() {
    val response = sharedViewModel.responseDetailState().value
    response?.let {
      binding.headersRecycle.adapter = adapter
      adapter.addData(response.headers)
      binding.urlTextView.text = response.request.url
      binding.responseCodeTextView.text = "code: ${response.responseCode.toString()}"
      binding.bodyTextView.text = response.body
    }
  }

}