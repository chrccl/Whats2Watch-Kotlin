package it.namenotfoundexception.whats2watch.model.repositories

import it.namenotfoundexception.whats2watch.model.dao.PreferenceDao
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Preference
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
    private val preferenceDao: PreferenceDao
) {

    suspend fun savePreference(preference: Preference) {
        preferenceDao.insertPreference(preference)
    }

    suspend fun getPreferencesByUser(code: String, username: String): List<Preference> {
        return preferenceDao.getPreferencesByUser(code, username)
    }

    suspend fun getRoomMatches(code: String): List<Movie>? {
        return preferenceDao.getRoomMatches(code)
    }

    suspend fun getPreferencesByRoom(code: String): List<Preference> {
        return preferenceDao.getPreferencesByRoom(code)
    }
}