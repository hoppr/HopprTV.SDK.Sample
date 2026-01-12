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

package com.hoppr.jetstream

import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.os.bundleOf
import com.hoppr.jetstream.data.repositories.MovieRepository
import com.hoppr.jetstream.data.repositories.MovieRepositoryImpl
import com.hoppr.hopprtvandroid.Hoppr
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import java.util.UUID

@HiltAndroidApp
class JetStreamApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Hoppr.init(
            this,
            userId = UUID.randomUUID().toString(),
            appKey = BuildConfig.APP_KEY,
            metadata = Bundle().apply {
                this.putString("userLevel", "Beginner")
                this.putBoolean("isPaidUser", true)
                this.putInt("userAge", 36)
                this.putBundle("userDetails", bundleOf().apply {
                    putString("email", "john.smith@gmail.com")
                    putString("firstName", "John")
                    putString("lastName", "Smith")
                })
            },
            onHostAction = { type, data ->
                if (type == "SHOW_ALERT") {
                    val message = "Alert with data: ${bundleToString(data)}"

                    // Ensure UI thread
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Bundle().apply {
                        putString("dataString", "Some String")
                        putBoolean("dataBool", true)
                    }
                } else {
                    null
                }
            })
    }
}

fun bundleToString(bundle: Bundle): String {
    return bundle.keySet()
        .joinToString(prefix = "{ ", postfix = " }") { key ->
            "$key=${bundle.get(key)}"
        }

}

@InstallIn(SingletonComponent::class)
@Module
abstract class MovieRepositoryModule {

    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
