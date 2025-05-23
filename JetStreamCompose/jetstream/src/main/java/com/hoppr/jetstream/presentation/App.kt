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

package com.hoppr.jetstream.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hoppr.jetstream.presentation.screens.Screens
import com.hoppr.jetstream.presentation.screens.categories.CategoryMovieListScreen
import com.hoppr.jetstream.presentation.screens.dashboard.DashboardScreen
import com.hoppr.jetstream.presentation.screens.movies.MovieDetailsScreen
import com.hoppr.jetstream.presentation.screens.videoPlayer.VideoPlayerScreen
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

@Composable
fun App(
    onBackPressed: () -> Unit
) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry) {
        val route = currentBackStackEntry?.destination?.route
        Log.d("NavigationTracker", "Navigated to: $route")

        when {
            route?.startsWith(Screens.MovieDetails()) == true -> {
                Log.d("NavigationTracker", "Entered Movie Details Screen")
            }
            currentBackStackEntry?.destination?.hierarchy?.none { it.route == Screens.MovieDetails() } == true -> {
                Log.d("NavigationTracker", "Exited Movie Details Screen")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screens.Dashboard(),
        builder = {
            composable(
                route = Screens.CategoryMovieList(),
                arguments = listOf(
                    navArgument(CategoryMovieListScreen.CategoryIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                CategoryMovieListScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    },
                    onMovieSelected = { movie ->
                        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, movie.toBundle().apply {
                            this.putString(HopprParameter.BUTTON_ID, "OpenMovieDetails")
                        }) {
                            navController.navigate(
                                Screens.MovieDetails.withArgs(movie.id)
                            )
                        }
                    }
                )
            }
            composable(
                route = Screens.MovieDetails(),
                arguments = listOf(
                    navArgument(MovieDetailsScreen.MovieIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                MovieDetailsScreen(
                    goToMoviePlayer = {
                        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, it.toBundle().apply {
                            this.putString(HopprParameter.BUTTON_ID, "WatchTrailer")
                        }) {
                            navController.navigate(Screens.VideoPlayer())
                        }
                    },
                    refreshScreenWithNewMovie = { movie ->
                        navController.navigate(
                            Screens.MovieDetails.withArgs(movie.id)
                        ) {
                            popUpTo(Screens.MovieDetails()) {
                                inclusive = true
                            }
                        }
                    },
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
            composable(route = Screens.Dashboard()) {
                DashboardScreen(
                    openCategoryMovieList = { categoryId ->
                        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, bundleOf().apply {
                            this.putString(HopprParameter.BUTTON_ID, "OpenCategory")
                            this.putString("categoryId", categoryId)
                        }){
                            navController.navigate(
                                Screens.CategoryMovieList.withArgs(categoryId)
                            )
                        }
                    },
                    openMovieDetailsScreen = { movieId ->
                        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, bundleOf().apply {
                            this.putString(HopprParameter.BUTTON_ID, "OpenMovieDetails")
                            this.putString("movieId", movieId)
                        }){
                            navController.navigate(
                                Screens.MovieDetails.withArgs(movieId)
                            )
                        }
                    },
                    openVideoPlayer = { movie ->
                        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, movie.toBundle().apply {
                            this.putString(HopprParameter.BUTTON_ID, "OpenVideoPlayer")
                        }) {
                            navController.navigate(Screens.VideoPlayer())
                        }
                    },
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }
                )
            }
            composable(route = Screens.VideoPlayer()) {
                VideoPlayerScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
        }
    )
}
