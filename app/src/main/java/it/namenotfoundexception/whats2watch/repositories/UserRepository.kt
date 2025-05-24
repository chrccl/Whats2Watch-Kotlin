package it.namenotfoundexception.whats2watch.repositories

import it.namenotfoundexception.whats2watch.model.dao.UserDao
import it.namenotfoundexception.whats2watch.model.entities.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return try {
            userDao.getUserByUsername(username)
        } catch (e: Exception) {
            null
        }
    }

}