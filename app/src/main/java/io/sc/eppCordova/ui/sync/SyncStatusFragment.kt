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
import io.sc.eppCordova.databinding.FragmentSyncStatusBinding
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

    private var _binding: FragmentSyncStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SyncStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = SyncQueueAdapter()
        binding.rvSyncQueue.adapter = adapter

        viewModel.pendingCount.observe(viewLifecycleOwner) { count ->
            binding.tvPendingCount.text = "$count नोंदी sync बाकी"
        }

        viewModel.pendingItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        binding.btnSyncNow.setOnClickListener {
            // Trigger WorkManager manually here
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPendingItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class SyncQueueAdapter : RecyclerView.Adapter<SyncQueueAdapter.ViewHolder>() {
        private var items = listOf<SyncQueueEntity>()

        fun submitList(newItems: List<SyncQueueEntity>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = io.sc.eppCordova.databinding.ItemSyncQueueBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.tvItemType.text = when(item.itemType) {
                "REGISTRATION" -> "पीक नोंदणी"
                "LOSS_CLAIM" -> "नुकसान दावा"
                else -> item.itemType
            }
            holder.binding.tvItemId.text = "ID: ${item.itemId}"
        }

        override fun getItemCount() = items.size
        class ViewHolder(val binding: io.sc.eppCordova.databinding.ItemSyncQueueBinding) : RecyclerView.ViewHolder(binding.root)
    }
}