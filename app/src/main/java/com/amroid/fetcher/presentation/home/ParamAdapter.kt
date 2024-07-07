package com.amroid.fetcher.presentation.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.amroid.fetcher.databinding.FileParamListRowItemBinding
import com.amroid.fetcher.databinding.TextParamListRowItemBinding
import com.amroid.fetcher.domain.entities.Param
import com.amroid.fetcher.domain.entities.ParamType
import com.amroid.fetcher.utils.watchText

class ParamAdapter(
  private val data: MutableList<Param> = mutableListOf<Param>(),
  private val onChooseFile: () -> Unit = {}
) : RecyclerView.Adapter<ViewHolder>() {
  var selectedPosition = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    if (viewType == ParamType.FILE.ordinal) {
      val textBinding = FileParamListRowItemBinding.inflate(inflater, parent, false)
      return FileParamViewHolder(textBinding)
    }

    val textBinding = TextParamListRowItemBinding.inflate(inflater, parent, false)
    return TextParamViewHolder(textBinding)

  }

  fun addEmptyTextParam() {
    data.add(Param(type = ParamType.TEXT))
    notifyItemInserted(data.size - 1)
  }

  fun addEmptyFileParam() {
    data.add(Param(type = ParamType.FILE))
    notifyItemInserted(data.size - 1)

  }

  fun clearParams() {
    data.clear()
    notifyDataSetChanged()
  }

  fun getParamList() = data.filter {
    it.key.isNotEmpty() && (it.value.isNotEmpty() || (it.isFile && it.fileUri.isNotEmpty()))
  }

  override fun getItemViewType(position: Int): Int {
    return data[position].type.ordinal
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (holder) {
      is TextParamViewHolder -> {
        holder.bindData(data[position])
      }

      is FileParamViewHolder -> {
        holder.bindData(data[position], onChooseFile)
      }

    }
  }

  fun addFileValue(uri: String) {
    data[selectedPosition].fileUri = uri
    notifyItemChanged(selectedPosition)
  }

  inner class TextParamViewHolder(private val binding: TextParamListRowItemBinding) :
    ViewHolder(binding.root) {
    fun bindData(param: Param) {
      binding.keyEdit.setText(param.key)
      binding.valueEdit.setText(param.value)
      binding.keyEdit.watchText {
        param.key = it.toString()
      }
      binding.valueEdit.watchText {
        param.value = it.toString()
      }
      binding.removeButton.setOnClickListener {
        data.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
      }
    }
  }

  inner class FileParamViewHolder(private val binding: FileParamListRowItemBinding) :
    ViewHolder(binding.root) {
    fun bindData(param: Param, onChooseFile: () -> Unit) {
      binding.keyEdit.setText(param.key)
      binding.valueEdit.setText(param.fileUri)
      binding.fileToggle.isChecked = param.isFile
      if (param.isFile) {
        binding.addFileButton.visibility = View.VISIBLE
        binding.valueEdit.isEnabled = false
      } else {
        binding.addFileButton.visibility = View.GONE
        binding.valueEdit.isEnabled = true
      }
      binding.keyEdit.watchText {
        param.key = it.toString()
      }
      binding.removeButton.setOnClickListener {
        data.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
      }
      binding.addFileButton.setOnClickListener {
        selectedPosition = adapterPosition
        onChooseFile()
      }
      binding.fileToggle.setOnClickListener {
        param.isFile = binding.fileToggle.isChecked
        notifyItemChanged(adapterPosition)
      }

    }
  }
}