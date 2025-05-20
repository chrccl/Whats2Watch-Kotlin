package it.namenotfoundexception.whats2watch.model.repositories

import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.entities.Review
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao
) {

    suspend fun saveReview(review: Review) {
        reviewDao.insertReview(review)
    }

    suspend fun getMovieReviews(id: String): List<Review> {
        return reviewDao.getMovieReviews(id)
    }

    suspend fun getReviewsByUser(username: String): List<Review> {
        return reviewDao.getReviewsByUser(username)
    }
}