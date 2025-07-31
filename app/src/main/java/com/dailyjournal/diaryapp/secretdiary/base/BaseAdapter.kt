package com.dailyjournal.diaryapp.secretdiary.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.widget.tap


abstract class BaseAdapter<VB : ViewBinding, M : Any> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var listData: MutableList<M> = arrayListOf()
    protected abstract fun createBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): VB

    protected abstract fun creatVH(binding: VB): RecyclerView.ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: VB = createBinding(LayoutInflater.from(parent.context), parent, viewType)
        return creatVH(binding)
    }

    override fun getItemCount(): Int = if (listData.size > 0) listData.size else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listData.size == 0) {
            holder.itemView.tap {
                Toast.makeText(
                    holder.itemView.context,
                    R.string.this_is_item_sample,
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        (holder as BaseVH<M>).bind(listData[position])
    }

    abstract inner class BaseVH<M>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root) {
        open fun onItemClickListener(data: M) = Unit
        open fun bind(data: M) {
            binding.root.tap { onItemClickListener(data) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(newList: MutableList<M>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }


}