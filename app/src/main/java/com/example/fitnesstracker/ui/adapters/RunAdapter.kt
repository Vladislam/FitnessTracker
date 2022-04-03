package com.example.fitnesstracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.databinding.ItemRunBinding
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RunAdapter(private val listener: ((item: RunEntity) -> Unit)? = null) :
    ListAdapter<RunEntity, RecyclerView.ViewHolder>(DiffCallback()) {

    private val recyclerJob = Job()
    private val recyclerScope = CoroutineScope(Dispatchers.Main + recyclerJob)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RunViewHolder(
            ItemRunBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RunViewHolder -> {
                holder.bind(currentList[position])
            }
        }
    }

    inner class RunViewHolder(
        private val binding: ItemRunBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.throttleFirstClicks(recyclerScope) {
                    if (adapterPosition != NO_POSITION) {
                        listener?.invoke(getItem(adapterPosition))
                    }
                }
            }
        }

        fun bind(item: RunEntity) = binding.apply {
            run = item
        }
    }

    override fun submitList(list: List<RunEntity>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class DiffCallback : DiffUtil.ItemCallback<RunEntity>() {
        override fun areItemsTheSame(oldItem: RunEntity, newItem: RunEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RunEntity, newItem: RunEntity) =
            oldItem == newItem
    }
}