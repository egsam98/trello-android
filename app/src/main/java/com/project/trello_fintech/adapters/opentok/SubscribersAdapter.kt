package com.project.trello_fintech.adapters.opentok

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.project.trello_fintech.R

/**
 * Адаптер участников видеоконференции
 * @property subscribers MutableList<Subscriber>
 */
class SubscribersAdapter: RecyclerView.Adapter<SubscribersAdapter.ViewHolder>() {

    private var subscribers = mutableListOf<Subscriber>()

    class ViewHolder(val subscriberView: FrameLayout): RecyclerView.ViewHolder(subscriberView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val subscriberView = LayoutInflater.from(parent.context).inflate(R.layout.subscriber_list_item, parent, false)
                as FrameLayout
        return ViewHolder(subscriberView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subscriberView.removeAllViews()
        holder.subscriberView.addView(subscribers[position].view)
    }

    override fun getItemCount() = subscribers.size

    fun register(subscriber: Subscriber) {
        subscribers.add(subscriber)
        notifyDataSetChanged()
    }

    fun deleteByStream(stream: Stream) {
        val subscriber = subscribers.find { it.stream == stream }
        subscribers.remove(subscriber)
        notifyDataSetChanged()
    }
}