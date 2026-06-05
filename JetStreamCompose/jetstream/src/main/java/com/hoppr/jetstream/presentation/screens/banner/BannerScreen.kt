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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoppr.jetstream.data.entities.Movie
import com.hoppr.jetstream.data.util.StringConstants
import com.hoppr.jetstream.presentation.common.Error
import com.hoppr.jetstream.presentation.common.Loading
import com.hoppr.jetstream.presentation.common.MoviesRow

@Composable
fun BannerScreen(
    onMovieClick: (movie: Movie) -> Unit = {},
    onScroll: (isTopBarVisible: Boolean) -> Unit = {},
    isTopBarVisible: Boolean = true,
    bannerScreenViewModel: BannerScreenViewModel = hiltViewModel(),
) {
    val uiState by bannerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is BannerScreenUiState.Ready -> {
            BannerCatalog(
                trendingMovies = s.trendingMovieList,
                nowPlayingMovies = s.nowPlayingMovieList,
                popularMovies = s.popularMovieList,
                dramaMovies = s.dramaMovieList,
                onMovieClick = onMovieClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }

        is BannerScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        is BannerScreenUiState.Error -> Error(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun BannerCatalog(
    trendingMovies: List<Movie>,
    nowPlayingMovies: List<Movie>,
    popularMovies: List<Movie>,
    dramaMovies: List<Movie>,
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        onScroll(scrollState.value == 0)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) scrollState.animateScrollTo(0)
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(bottom = 108.dp),
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        BannerCarousel(autoScroll=true)

        Spacer(modifier = Modifier.height(10.dp))

        MoviesRow(
            modifier = Modifier.padding(top = 10.dp),
            movieList = trendingMovies,
            title = StringConstants.Composable.HomeScreenTrendingTitle,
            onMovieSelected = onMovieClick,
        )

        MoviesRow(
            modifier = Modifier.padding(top = 10.dp),
            movieList = nowPlayingMovies,
            title = StringConstants.Composable.HomeScreenNowPlayingMoviesTitle,
            onMovieSelected = onMovieClick,
        )

        MoviesRow(
            modifier = Modifier.padding(top = 10.dp),
            movieList = popularMovies,
            title = StringConstants.Composable.PopularFilmsThisWeekTitle,
            onMovieSelected = onMovieClick,
        )

        MoviesRow(
            modifier = Modifier.padding(top = 10.dp),
            movieList = dramaMovies,
            title = StringConstants.Composable.BingeWatchDramasTitle,
            onMovieSelected = onMovieClick,
        )
    }
}