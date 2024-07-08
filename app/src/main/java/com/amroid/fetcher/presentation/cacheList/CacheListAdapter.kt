package com.amroid.fetcher.presentation.cacheList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amroid.fetcher.databinding.CacheListRowItemBinding
import com.amroid.fetcher.domain.entities.Response

class CacheListAdapter(
  private val data: MutableList<Response> = mutableListOf(),
  private val onClick: (Response) -> Unit = {}
) : RecyclerView.Adapter<CacheListAdapter.RequestViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val textBinding = CacheListRowItemBinding.inflate(inflater, parent, false)
    return RequestViewHolder(textBinding)

  }


  override fun getItemCount(): Int {
    return data.size
  }

  override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
    holder.bindData(data[position])


  }

  fun addData(newData: List<Response>) {
    data.clear()
    data.addAll(newData)
    notifyDataSetChanged()
  }

  inner class RequestViewHolder(private val binding: CacheListRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(response: Response) {
      binding.methodTypeLabel.text = response.request.requestType.name
      binding.urlLabel.text = response.request.url
      binding.resposne.text = response.responseCode.toString()
      if (response.isSuccess) {
        binding.resposne.setBackgroundColor(Color.GREEN)
      } else {
        binding.resposne.setBackgroundColor(Color.RED)
      }
      binding.time.text = "executionTime: ${(response.executionTime / 1000f)} s"
      binding.root.setOnClickListener {
        onClick(response)

      }
    }
  }
}