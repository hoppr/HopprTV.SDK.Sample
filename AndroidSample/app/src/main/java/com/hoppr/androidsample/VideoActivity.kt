package com.hoppr.androidsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class VideoActivity : AppCompatActivity() {
    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "VideoScreen")
        this.putString("MOVIE_NAME", "Spider-Man")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)

        val playerView: PlayerView = findViewById(R.id.playerView)
        val player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Build the media item.
        val mediaItem =
            MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle)
    }

    override fun onDestroy() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onDestroy()
    }
}
