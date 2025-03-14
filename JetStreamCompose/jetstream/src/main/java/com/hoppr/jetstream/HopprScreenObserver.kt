package com.hoppr.jetstream

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class HopprScreenObserver(private val bundle: Bundle) : ViewModel(), DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume $bundle")
        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, bundle)
        super.onResume(owner)
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "onCreate $bundle")
        super.onCreate(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy $bundle")
        super.onDestroy(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart $bundle")
        super.onStart(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "onPause $bundle")
        super.onPause(owner)
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared $bundle")
        super.onCleared()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop $bundle")
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, bundle)
        super.onStop(owner)
    }
    
    private companion object{
        private const val TAG = "HopprScreenObserver"
    }
}