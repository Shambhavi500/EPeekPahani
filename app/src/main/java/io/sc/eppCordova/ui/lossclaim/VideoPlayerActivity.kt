package io.sc.eppCordova.ui.lossclaim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri = intent.getStringExtra("VIDEO_URI") ?: ""
        
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        
        if (videoUri.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(videoUri)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
