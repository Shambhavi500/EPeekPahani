package io.sc.eppCordova.utils

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import io.sc.eppCordova.R
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineBannerHelper @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val syncQueueDao: SyncQueueDao
) {
    fun attach(bannerView: View, viewLifecycleOwner: LifecycleOwner) {
        val tvMessage = bannerView.findViewById<TextView>(R.id.tv_offline_message)
        
        networkUtils.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                bannerView.visibility = View.GONE
            } else {
                bannerView.visibility = View.VISIBLE
            }
        }
        
        syncQueueDao.getPendingSyncCount().observe(viewLifecycleOwner) { count ->
            tvMessage.text = "ऑफलाइन — $count नोंदी sync बाकी"
        }

        bannerView.setOnClickListener {
            // Only navigate if it's currently showing
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