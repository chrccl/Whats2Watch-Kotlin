package it.namenotfoundexception.whats2watch.repositories

import it.namenotfoundexception.whats2watch.api.TMDBService
import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.entities.Review
import it.namenotfoundexception.whats2watch.model.toReviewEntity
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao,
    private val api: TMDBService
) {

    suspend fun saveReview(review: Review) {
        reviewDao.insertReview(review)
    }

    suspend fun getMovieReviews(movieId: String): List<Review> {
        val localReviews = reviewDao.getMovieReviews(movieId)

        val tmdbReviews = try {
            val tmdbId = movieId.toInt()
            val response = api.getMovieReviews(tmdbId)
            response.results.map { it.toReviewEntity(movieId.toString()) }
        } catch (_: Exception) {
            emptyList<Review>()
        }
        return localReviews + tmdbReviews
    }

}