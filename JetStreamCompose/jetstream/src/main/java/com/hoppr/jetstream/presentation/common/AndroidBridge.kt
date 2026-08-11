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

import android.webkit.JavascriptInterface

/**
 * Bridge exposed to the banner creative's JavaScript as `window.AndroidBridge`.
 *
 * The Ad server banner HTML calls [onAdLoaded] once the creative is fetched
 * and displayed, or [onAdFailed] on a no-fill / network / parsing error. These
 * methods are invoked on the WebView's JS bridge thread (NOT the main thread),
 * so callbacks must hop back to the main thread before touching UI/Compose state.
 */
class AndroidBridge(
    private val onAdLoaded: () -> Unit = {},
    private val onAdFailed: (String) -> Unit = {}
) {
    @JavascriptInterface
    fun onAdLoaded() {
        onAdLoaded.invoke()
    }

    @JavascriptInterface
    fun onAdFailed(message: String) {
        onAdFailed.invoke(message)
    }
}
