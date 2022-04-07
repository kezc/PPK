package com.put.ubi.fundslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.put.ubi.databinding.ItemFundBinding
import com.put.ubi.model.Fund

class FundsListAdapter(private val onClickListener: (Fund) -> Unit) :
    ListAdapter<Fund, FundsListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFundBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemFundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fund: Fund) {
            binding.name.text = fund.name
            binding.root.setOnClickListener { onClickListener(fund) }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Fund>() {
            override fun areItemsTheSame(oldItem: Fund, newItem: Fund): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Fund, newItem: Fund): Boolean {
                return oldItem == newItem
            }
        }
    }
}
