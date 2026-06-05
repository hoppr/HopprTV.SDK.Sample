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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoppr.jetstream.data.entities.MovieList
import com.hoppr.jetstream.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BannerScreenViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {
    val uiState: StateFlow<BannerScreenUiState> = combine(
        movieRepository.getTrendingMovies(),
        movieRepository.getNowPlayingMovies(),
        movieRepository.getPopularFilmsThisWeek(),
        movieRepository.getBingeWatchDramas(),
    ) { trending, nowPlaying, popular, dramas ->
        BannerScreenUiState.Ready(
            trendingMovieList = trending,
            nowPlayingMovieList = nowPlaying,
            popularMovieList = popular,
            dramaMovieList = dramas,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BannerScreenUiState.Loading
    )
}

sealed interface BannerScreenUiState {
    data object Loading : BannerScreenUiState
    data object Error : BannerScreenUiState
    data class Ready(
        val trendingMovieList: MovieList,
        val nowPlayingMovieList: MovieList,
        val popularMovieList: MovieList,
        val dramaMovieList: MovieList,
    ) : BannerScreenUiState
}
