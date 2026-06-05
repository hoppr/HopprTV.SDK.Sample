package com.hoppr.jetstream.presentation.screens.banner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CarouselDotIndicators(itemCount: Int, currentIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(itemCount) { index ->
            val isActive = index == currentIndex
            val dotWidth by animateDpAsState(
                targetValue = if (isActive) 24.dp else 6.dp,
                animationSpec = tween(300),
                label = "dotWidth"
            )
            val dotColor by animateColorAsState(
                targetValue = if (isActive) Color.White else Color.White.copy(alpha = 0.35f),
                animationSpec = tween(300),
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 4.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}
