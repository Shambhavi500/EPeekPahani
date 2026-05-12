package io.sc.eppCordova.utils

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.databinding.OfflineBannerBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineBannerHelper @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val syncQueueDao: SyncQueueDao
) {
    fun attach(bannerView: View, viewLifecycleOwner: LifecycleOwner) {
        val binding = OfflineBannerBinding.bind(bannerView)
        
        networkUtils.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                bannerView.visibility = View.GONE
            } else {
                bannerView.visibility = View.VISIBLE
            }
        }
        
        syncQueueDao.getPendingSyncCount().observe(viewLifecycleOwner) { count ->
            binding.tvOfflineMessage.text = "ऑफलाइन — $count नोंदी sync बाकी"
        }

        bannerView.setOnClickListener {
            if (bannerView.visibility == View.VISIBLE) {
                try {
                    it.findNavController().navigate(R.id.action_dashboard_to_syncStatus)
                } catch (e: Exception) {
                    // Ignore if not in a destination that supports this action
                }
            }
        }
    }
}