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

package com.google.jetstream.data.entities

import androidx.core.os.bundleOf

data class MovieDetails(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val posterUri: String,
    val name: String,
    val description: String,
    val pgRating: String,
    val releaseDate: String,
    val categories: List<String>,
    val duration: String,
    val director: String,
    val screenplay: String,
    val music: String,
    val castAndCrew: List<MovieCast>,
    val status: String,
    val originalLanguage: String,
    val budget: String,
    val revenue: String,
    val similarMovies: MovieList,
    val reviewsAndRatings: List<MovieReviewsAndRatings>
){
    fun toBundle() = bundleOf().apply {
        putString("movieName", name)
        putString("movieDuration", duration)
        putString("movieReleaseDate", releaseDate)
        putString("movieMusic", music)
        putString("moviePgRating", pgRating)
        putString("movieBudget", budget)
        putString("movieDescription", description)
        putString("movieDirector", director)
        putString("movieId", id)
        putString("movieOriginalLanguage", originalLanguage)
        putString("movieRevenue", revenue)
        putString("movieScreenPlay", screenplay)
        putString("movieStatus", status)
        putParcelableArray(
            "movieCastAndCrew",
            castAndCrew.map { bundleOf().apply {
                this.putString("id", it.id)
                this.putString("realName", it.realName)
                this.putString("characterName", it.characterName)
            } }.toTypedArray()
        )
        putStringArray("movieCategories", categories.toTypedArray())
        putParcelableArray(
            "movieReviewsAndRatings",
            reviewsAndRatings.map { bundleOf().apply {
                this.putString("reviewerName", it.reviewerName)
                this.putString("reviewCount", it.reviewCount)
                this.putString("reviewRating", it.reviewRating)
            } }.toTypedArray()
        )
        putStringArray(
            "movieSimilarMovies",
            similarMovies.map { it.name }.toTypedArray()
        )
    }
}
