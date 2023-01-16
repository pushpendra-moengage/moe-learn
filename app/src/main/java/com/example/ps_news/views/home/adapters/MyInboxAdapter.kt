package com.example.ps_news.views.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.ps_news.R
import com.moengage.inbox.core.model.InboxMessage
import com.moengage.inbox.ui.adapter.InboxAdapter
import com.moengage.inbox.ui.adapter.InboxListAdapter
import com.moengage.inbox.ui.adapter.ViewHolder

class MyInboxAdapter: InboxAdapter() {
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemViewType(position: Int, inboxMessage: InboxMessage): Int {
        return 1
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        position: Int,
        inboxMessage: InboxMessage,
        inboxListAdapter: InboxListAdapter
    ) {
        (viewHolder as MyViewHolder).setdata(inboxMessage)
        viewHolder.itemView.setOnClickListener {
            inboxListAdapter.onItemClicked(position, inboxMessage)
        }
        (viewHolder as MyViewHolder).btnDelete.setOnClickListener {
            inboxListAdapter.deleteItem(position, inboxMessage)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.vh_my_inbox_row, viewGroup, false)
        return MyViewHolder(view)
    }

    inner class MyViewHolder(view: View): ViewHolder(view){

        private val tvTitle: TextView = view.findViewById(R.id.tv_title)
        private val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val btnDelete: ImageButton = view.findViewById(R.id.ib_delete)


        fun setdata(inboxMessage: InboxMessage){
            tvTitle.text = inboxMessage.textContent.title
            tvDesc.text = inboxMessage.textContent?.let {
                it.summary + it.message
            }
        }
    }
}