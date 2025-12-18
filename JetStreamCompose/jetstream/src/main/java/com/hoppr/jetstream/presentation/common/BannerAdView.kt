/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hoppr.jetstream.presentation.common

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.external.banner.BannerAdResult

private const val TAG = "BannerAdView"

/**
 * A composable that displays a banner ad from the Hoppr SDK.
 * The banner takes no space until it's successfully loaded.
 *
 * @param adUnit The ad unit identifier for the banner ad request
 * @param modifier Modifier to be applied to the banner
 */
@Composable
fun BannerAdView(
    adUnit: String = "Banner",
    modifier: Modifier = Modifier
) {
    var bannerAdData by remember { mutableStateOf<BannerAdResult.Success?>(null) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    when (val result = Hoppr.requestBannerAd(adUnit)) {
        is BannerAdResult.Success -> {
            val banner = result.bannerAdData
            Log.d(TAG, "Banner ad loaded successfully!")
            Log.d(TAG, "Banner dimensions: ${banner.width}x${banner.height}")
            Log.d(TAG, "Banner content: ${banner.baseUrl} - ${banner.content}")
            bannerAdData = result
        }
        is BannerAdResult.Error -> {
            Log.e(TAG, "Failed to load banner ad: ${result.message}")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
            webView = null
        }
    }

    // Only render the banner if ad data is available
    bannerAdData?.let { adResult ->
        val banner = adResult.bannerAdData

        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {val density = LocalDensity.current

            val bannerWidthDp = with(density) { banner.width.toDp() }
            val bannerHeightDp = with(density) { banner.height.toDp() }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        isFocusable = false
                        isFocusableInTouchMode = false
                        loadDataWithBaseURL(
                            banner.baseUrl,
                            banner.content,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webView = this
                    }
                },
                modifier = Modifier
//                    .width(bannerWidthDp)
//                    .height(bannerHeightDp)
                    .focusProperties { canFocus = false }
            )
        }
    }
}
