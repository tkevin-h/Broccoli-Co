package com.thavin.email_invitations.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thavin.email_invitations.R
import com.thavin.email_invitations.data.remote.cat_facts.model.CatFacts

class TestAdapter :
    ListAdapter<CatFacts, TestAdapter.TestViewHolder>(TestDiffCallback) {

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.text_view_fact)
        private var currentItem: CatFacts? = null

        fun bind(facts: CatFacts) {
            currentItem = facts
            text.text = facts.fact
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cat_facts, parent, false)

        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

object TestDiffCallback : DiffUtil.ItemCallback<CatFacts>() {
    override fun areItemsTheSame(oldItem: CatFacts, newItem: CatFacts): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CatFacts, newItem: CatFacts): Boolean {
        return oldItem.id == newItem.id
    }
}