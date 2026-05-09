package io.sc.eppCordova.ui.lossclaim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.sc.eppCordova.R

data class ChatMessage(val text: String, val isAi: Boolean)

class AiChatAdapter : RecyclerView.Adapter<AiChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isAi) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // We'll dynamically create simple text views or use a custom layout.
        // For simplicity, inflating a custom layout would be better, but we can do it programmatically
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]
        val tv = holder.itemView.findViewById<TextView>(android.R.id.text1)
        if (msg.isAi) {
            tv.text = "🤖: ${msg.text}"
            tv.setTextColor(android.graphics.Color.parseColor("#1a6b3a"))
            tv.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        } else {
            tv.text = "✅: ${msg.text}"
            tv.setTextColor(android.graphics.Color.BLACK)
            tv.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        }
        tv.setPadding(32, 16, 32, 16)
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}