package com.hoppr.jetstream

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun ObserveHopprScreen(bundle: Bundle) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val hopprObserver = remember {
        HopprScreenObserver(bundle)
    }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(hopprObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(hopprObserver)
        }
    }
}