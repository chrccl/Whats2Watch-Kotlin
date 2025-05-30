package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.R
import it.namenotfoundexception.whats2watch.model.ResourceProvider
import it.namenotfoundexception.whats2watch.model.entities.User
import it.namenotfoundexception.whats2watch.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val res: ResourceProvider
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun register(username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = User(username = username, password = password)
                userRepo.saveUser(user)
                _currentUser.value = user
                _authError.value = null
            } catch (e: Exception) {
                _authError.value = res.getString(R.string.registration_failed, e.message)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepo.getUserByUsername(username)
                if (user != null && user.password == password) {
                    _currentUser.value = user
                    _authError.value = null
                } else {
                    _authError.value = res.getString(R.string.credentials_are_not_valid)
                }
            } catch (e: Exception) {
                _authError.value = res.getString(R.string.login_failed, e.message)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authError.value = null
    }
}