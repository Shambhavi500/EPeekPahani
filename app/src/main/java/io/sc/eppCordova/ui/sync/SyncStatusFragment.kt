package io.sc.eppCordova.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val syncQueueDao: SyncQueueDao
) : ViewModel() {

    private val _pendingItems = MutableLiveData<List<SyncQueueEntity>>()
    val pendingItems: LiveData<List<SyncQueueEntity>> = _pendingItems

    val pendingCount = syncQueueDao.getPendingSyncCount()

    fun loadPendingItems() {
        viewModelScope.launch {
            _pendingItems.postValue(syncQueueDao.getPendingItems())
        }
    }
}

@AndroidEntryPoint
class SyncStatusFragment : Fragment() {

    private val viewModel: SyncStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sync_status, container, false)
        
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val rv = view.findViewById<RecyclerView>(R.id.rv_sync_queue)
        val adapter = SyncQueueAdapter()
        rv.adapter = adapter

        viewModel.pendingCount.observe(viewLifecycleOwner) { count ->
            view.findViewById<TextView>(R.id.tv_pending_count).text = "$count नोंदी sync बाकी"
        }

        viewModel.pendingItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        view.findViewById<View>(R.id.btn_sync_now).setOnClickListener {
            // Trigger WorkManager manually here
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPendingItems()
    }

    class SyncQueueAdapter : RecyclerView.Adapter<SyncQueueAdapter.ViewHolder>() {
        private var items = listOf<SyncQueueEntity>()

        fun submitList(newItems: List<SyncQueueEntity>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_sync_queue, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemView.findViewById<TextView>(R.id.tv_item_type).text = when(item.itemType) {
                "REGISTRATION" -> "पीक नोंदणी"
                "LOSS_CLAIM" -> "नुकसान दावा"
                else -> item.itemType
            }
            holder.itemView.findViewById<TextView>(R.id.tv_item_id).text = "ID: ${item.itemId}"
        }

        override fun getItemCount() = items.size
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    }
}