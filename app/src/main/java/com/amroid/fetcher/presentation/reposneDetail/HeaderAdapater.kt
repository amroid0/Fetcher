package com.amroid.fetcher.presentation.reposneDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amroid.fetcher.databinding.ListHeaderRowItemBinding
import com.amroid.fetcher.domain.entities.Param

class HeaderAdapter(private val data: MutableList<Param> = mutableListOf()) :
  RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val textBinding = ListHeaderRowItemBinding.inflate(inflater, parent, false)
    return HeaderViewHolder(textBinding)

  }


  override fun getItemCount(): Int {
    return data.size
  }

  override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
    holder.bindData(data[position])


  }

  fun addData(newData: List<Param>) {
    data.clear()
    data.addAll(newData)
    notifyDataSetChanged()
  }

  inner class HeaderViewHolder(private val binding: ListHeaderRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(param: Param) {
      binding.keyLabel.text = param.key
      binding.valueLabel.text = param.value

    }
  }
}