package com.hoppr.androidsample

import android.app.Application
import android.os.Bundle
import com.hoppr.hopprtvandroid.Hoppr

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Hoppr.init(
            application = this,
            appId = "TestOperator2",
            apiKey = "TestApiKey",
            userId = "OperatorUserId2",
            activity = null,
            metadata = Bundle().apply {
                this.putString("MetaData1", "MetaDataValue")
                this.putInt("MetaData2", 2)
                this.putBoolean("MetaData3", true)
            })
    }
}