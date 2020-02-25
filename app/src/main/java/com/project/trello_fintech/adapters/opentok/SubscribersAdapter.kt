package com.project.trello_fintech.adapters.opentok

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.project.trello_fintech.R
import com.project.trello_fintech.views.ExpandableConstraintLayout


/**
 * Адаптер участников видеоконференции
 * @property subscribers MutableList<Subscriber>
 */
class SubscribersAdapter: RecyclerView.Adapter<SubscribersAdapter.ViewHolder>() {

    private var subscribers = mutableListOf<Subscriber>()

    class ViewHolder(val subscriberWrapper: ExpandableConstraintLayout): RecyclerView.ViewHolder(subscriberWrapper) {
        val subscriberView: FrameLayout = subscriberWrapper.findViewById(R.id.subscriber)
        val usernameView: TextView = subscriberWrapper.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val subscriberWrapperView = LayoutInflater.from(parent.context).inflate(R.layout.subscriber_list_item, parent, false)
                as ExpandableConstraintLayout
        return ViewHolder(subscriberWrapperView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subscriber = subscribers[position]
        with (holder) {
            usernameView.text = subscriber.stream.name
            subscriberView.removeAllViews()
            subscriberView.addView(subscriber.view)
            subscriberView.setOnClickListener {
                subscriberWrapper.expandOrRestore()
                subscribers.add(0, subscriber)
                subscribers.removeAt(position)
                notifyItemMoved(position, 0)
            }
        }
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