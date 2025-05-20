package it.namenotfoundexception.whats2watch.model.repository

import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Reviews
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDao : ReviewDao
) {

    suspend fun saveReview(review: Reviews){
        reviewDao.insertReview(review)
    }

    suspend fun getMovieRevies(id: String): List<Movie>{
        return reviewDao.getMovieReviews(id)
    }

    suspend fun getReviewsByUser(username: String): List<Movie> {
        return reviewDao.getReviewsByUser(username)
    }
}