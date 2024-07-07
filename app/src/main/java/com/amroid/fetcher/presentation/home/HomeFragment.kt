package com.amroid.fetcher.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amroid.fetcher.R
import com.amroid.fetcher.databinding.FragmentHomeBinding
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.RequestType
import com.amroid.fetcher.utils.FilePicker

class HomeFragment : Fragment() {
  private lateinit var filePickerLauncher: ActivityResultLauncher<String>
  private var _binding: FragmentHomeBinding? = null
  private val headerAdapter: ParamAdapter by lazy {
    ParamAdapter()
  }
  private val requestParamAdapter: ParamAdapter by lazy {
    ParamAdapter(onChooseFile = {
      filePickerLauncher.launch("*/*")

    })
  }
  private val viewModel by lazy {
    ViewModelProvider(this, HomeViewModel.Factory)[HomeViewModel::class.java]
  }
  private val binding
    get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    filePickerLauncher = registerForActivityResult(FilePicker(requireContext())) { uris ->
      if (uris.isNotEmpty()) {
        requestParamAdapter.addFileValue(uris[0])
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return _binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
    setupLisenters()


  }

  private fun setupLisenters() {
    binding.methodRadioGroup.setOnCheckedChangeListener { _, id ->
      requestParamAdapter.clearParams()
      when (id) {
        R.id.get_radio -> {
          binding.requestBodyRadioGroup.visibility = View.INVISIBLE
          binding.rawBodyEdit.visibility = View.GONE
          binding.rawBodyTextView.visibility = View.GONE
          binding.paramsRecycleView.visibility = View.VISIBLE
          binding.addParamsButton.visibility = View.VISIBLE
          binding.paramsTextView.visibility = View.VISIBLE
        }

        R.id.post_radio -> {
          binding.requestBodyRadioGroup.visibility = View.VISIBLE
          binding.multiPartRadio.isChecked = true
        }
      }

    }

    binding.requestBodyRadioGroup.setOnCheckedChangeListener { _, id ->
      when (id) {
        R.id.multi_part_radio -> {
          binding.rawBodyEdit.visibility = View.GONE
          binding.rawBodyTextView.visibility = View.GONE
          binding.paramsRecycleView.visibility = View.VISIBLE
          binding.addParamsButton.visibility = View.VISIBLE
          binding.paramsTextView.visibility = View.VISIBLE
        }

        R.id.raw_body_radio -> {
          binding.rawBodyEdit.visibility = View.VISIBLE
          binding.rawBodyTextView.visibility = View.VISIBLE
          binding.paramsRecycleView.visibility = View.GONE
          binding.addParamsButton.visibility = View.GONE
          binding.paramsTextView.visibility = View.GONE
        }
      }

    }
    binding.addHeaderButton.setOnClickListener {
      headerAdapter.addEmptyTextParam()
    }
    binding.addParamsButton.setOnClickListener {
      if (binding.getRadio.isChecked) {
        requestParamAdapter.addEmptyTextParam()
      } else {
        requestParamAdapter.addEmptyFileParam()
      }
    }


    binding.searchButton.setOnClickListener {


      val request = Request(
        url = binding.urlEdit.toString(),
        requestType = if (binding.getRadio.isChecked) RequestType.GET else RequestType.POST,
        headers = if (binding.getRadio.isChecked) headerAdapter.getParamList() else emptyList(),
        rawBody = if (binding.rawBodyRadio.isChecked) binding.rawBodyEdit.toString() else "",
        formData = if (binding.multiPartRadio.isChecked) requestParamAdapter.getParamList() else emptyList()

      )
      viewModel.sendRequest(request)


    }

  }


  private fun initViews() {
    binding.headerRecycleView.apply {
      adapter = headerAdapter
    }
    binding.paramsRecycleView.apply {
      adapter = requestParamAdapter
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}