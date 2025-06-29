package com.example.kopipedia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kopipedia.databinding.ItemCoffeeBinding
import com.example.kopipedia.domain.model.Coffee

class CoffeeAdapter(private val onItemClick: (Coffee) -> Unit) :
    ListAdapter<Coffee, CoffeeAdapter.CoffeeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val binding = ItemCoffeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoffeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffee = getItem(position)
        if (coffee != null) {
            holder.bind(coffee)
        }
    }

    inner class CoffeeViewHolder(private val binding: ItemCoffeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(coffee: Coffee) {
            binding.tvCoffeeTitle.text = coffee.title
            binding.tvCoffeeDescription.text = coffee.description
            Glide.with(binding.root.context)
                .load(coffee.imageUrl)
                .into(binding.ivCoffeeImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Coffee>() {
            override fun areItemsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Coffee, newItem: Coffee): Boolean {
                return oldItem == newItem
            }
        }
    }
}
