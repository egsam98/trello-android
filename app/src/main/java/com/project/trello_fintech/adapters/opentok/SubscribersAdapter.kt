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


private class SubscriberName(val subscriber: Subscriber, val name: String)

/**
 * Адаптер участников видеоконференции
 * @property subscriberNames MutableList<Subscriber>
 */
class SubscribersAdapter: RecyclerView.Adapter<SubscribersAdapter.ViewHolder>() {

    private var subscriberNames = mutableListOf<SubscriberName>()

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
        val subscriberName = subscriberNames[position]
        with (holder) {
            usernameView.text = subscriberName.name
            subscriberView.removeAllViews()
            subscriberView.addView(subscriberName.subscriber.view)
            subscriberView.setOnClickListener {
                subscriberWrapper.expandOrRestore()
                subscriberNames.add(0, subscriberName)
                subscriberNames.removeAt(position)
                notifyItemMoved(position, 0)
            }
        }
    }

    override fun getItemCount() = subscriberNames.size

    fun register(subscriber: Subscriber, name: String) {
        subscriberNames.add(SubscriberName(subscriber, name))
        notifyDataSetChanged()
    }

    fun deleteByStream(stream: Stream) {
        val subscriber = subscriberNames.find { it.subscriber.stream == stream }
        subscriberNames.remove(subscriber)
        notifyDataSetChanged()
    }
}