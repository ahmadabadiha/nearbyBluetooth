package com.example.nearbybluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbybluetooth.databinding.ItemBinding

class RecyclerAdapter : ListAdapter<String, RecyclerAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun fill(item: String?) {
            binding.tv.text = item
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemBinding.inflate(
            LayoutInflater
                .from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fill(getItem(position))
    }
}

class DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
