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

package com.hoppr.jetstream.presentation.screens.banner

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.hoppr.jetstream.presentation.common.CarouselBannerAdView
import kotlinx.coroutines.delay
import kotlin.collections.copy
import kotlin.time.Duration.Companion.milliseconds

/** All slots are ads using the same ad unit — separate requests, unique responses. */
const val BANNER_AD_UNIT = "Banner"
const val BANNER_SLOT_COUNT = 3

/**
 * 3-slot peek carousel: center card is large (landscape), the side peeks are
 * small (portrait). Each ad keeps a STABLE composition position (key by ad
 * index), so its WebView is created once and only repositioned/resized as it
 * moves left -> center -> right. No destroy, no reload, no flash.
 *
 * NOTE: impression gating (fire trackers only when an ad is centered & fully
 * visible) is handled entirely inside the banner HTML, not here.
 */
@Composable
fun BannerCarousel(
    modifier: Modifier = Modifier,
    autoScroll: Boolean = true
) {
    val itemCount = BANNER_SLOT_COUNT
    var currentIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    // Auto-advance every 5 seconds only when autoScroll is true.
    // LaunchedEffect re-runs when autoScroll changes — cancels the
    // loop immediately when false, restarts it when true.
    LaunchedEffect(autoScroll) {
        if (!autoScroll) return@LaunchedEffect
        while (true) {
            delay(5000.milliseconds)
            currentIndex = (currentIndex + 1) % itemCount
        }
    }

    val prevIndex = (currentIndex - 1 + itemCount) % itemCount

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clipToBounds()
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.DirectionLeft -> {
                                currentIndex = (currentIndex - 1 + itemCount) % itemCount
                                true
                            }

                            Key.DirectionRight -> {
                                currentIndex = (currentIndex + 1) % itemCount
                                true
                            }

                            else -> false
                        }
                    } else false
                }
                .focusable()
        ) {
            // Every card is the SAME landscape size & creative ratio
            // (1280 x 360). The side cards are pushed mostly off-screen so only
            // a clean edge "peeks" in; the parent clipsToBounds hides the rest.
            // This keeps the banner undistorted in every slot — the center is
            // never cut, and the peeks show a natural banner edge.
            val peekVisible = 56.dp
            val spacing = 12.dp
            val centerW = this.maxWidth - peekVisible * 2 - spacing * 2
            // Taller "hero" card. The creative is 1280x360 (wide/short), so the
            // image fills this taller box via object-fit: cover (cropping the
            // sides). Tweak cardHeight to taste.
            val centerH = 205.dp

            repeat(itemCount) { i ->
                val isCenter = i == currentIndex
                val isLeft = i == prevIndex
                val targetX = when {
                    // center sits after the left peek + spacing
                    isCenter -> peekVisible + spacing
                    // left card: pushed left so only its right edge peeks in
                    isLeft -> peekVisible - centerW
                    // right card: pushed right so only its left edge peeks in
                    else -> peekVisible + spacing + centerW + spacing
                }
                val targetAlpha = if (isCenter) 1f else 0.5f
                // Side cards are scaled down so they read as smaller than the
                // center hero. Scaling toward each card's inner edge keeps the
                // peek anchored to the screen edge.
                val targetScale = if (isCenter) 1f else 0.85f

                val animX = animateDpAsState(targetX, tween(300), label = "x$i")
                val animAlpha = animateFloatAsState(targetAlpha, tween(300), label = "a$i")
                val animScale = animateFloatAsState(targetScale, tween(300), label = "s$i")

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        // Lambda form: value read during layout phase, no recomposition
                        .offset { IntOffset(animX.value.roundToPx(), 0) }
                        .width(centerW)
                        .height(centerH)
                        .graphicsLayer {
                            alpha = animAlpha.value
                            scaleX = animScale.value
                            scaleY = animScale.value
                            transformOrigin = when {
                                isCenter -> androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
                                isLeft -> androidx.compose.ui.graphics.TransformOrigin(1f, 0.5f)
                                else -> androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                            }
                            // Clip inside graphicsLayer so it applies to the
                            // offscreen texture — correctly rounds ALL content
                            // including WebViews (AndroidView).
                            shape = RoundedCornerShape(5.dp)
                            clip = true
                        }
                ) {
                    // Each slot owns its ad request via BannerAdView. The stable
                    // key(i) keeps the same WebView alive across reposition
                    // (no destroy, no reload, no flash). All cards use the same
                    // size, so "center" alignment is correct everywhere — the
                    // visible edge comes from the off-screen clipping, not a crop.
                    key(i) {
                        CarouselBannerAdView(
                            adUnit = BANNER_AD_UNIT,
                            isCentered = isCenter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        CarouselDotIndicators(itemCount = itemCount, currentIndex = currentIndex)
    }
}

