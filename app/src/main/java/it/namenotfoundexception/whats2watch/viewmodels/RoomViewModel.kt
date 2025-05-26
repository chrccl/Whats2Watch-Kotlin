package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.R
import it.namenotfoundexception.whats2watch.model.ResourceProvider
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.model.entities.RoomParticipant
import it.namenotfoundexception.whats2watch.model.entities.RoomWithUsers
import it.namenotfoundexception.whats2watch.repositories.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepo: RoomRepository,
    private val res: ResourceProvider
) : ViewModel() {

    private val _roomData = MutableStateFlow<RoomWithUsers?>(null)
    val roomData: StateFlow<RoomWithUsers?> = _roomData

    private val _roomByUsers = MutableStateFlow<List<Room>?>(null)
    val roomByUsers: StateFlow<List<Room>?> = _roomByUsers

    private val _roomError = MutableStateFlow<String?>(null)
    val roomError: StateFlow<String?> = _roomError

    fun createRoom(code: String, hostUsername: String) {
        viewModelScope.launch {
            try {
                roomRepo.saveRoom(Room(code = code, usernameHost = hostUsername))
                roomRepo.insertParticipant(
                    RoomParticipant(
                        roomCode = code,
                        username = hostUsername
                    )
                )
                _roomError.value = null
            } catch (e: Exception) {
                _roomError.value = res.getString(R.string.error_creating_a_room, e.message)
            }
        }
    }

    fun joinRoom(code: String, username: String) {
        viewModelScope.launch {
            try {
                roomRepo.insertParticipant(RoomParticipant(roomCode = code, username = username))
                _roomError.value = null
                loadRoom(code)
            } catch (e: Exception) {
                _roomError.value = res.getString(R.string.error_during_the_join, e.message)
            }
        }
    }

    fun leaveRoom(code: String, username: String) {
        viewModelScope.launch {
            try {
                roomRepo.removeParticipant(code, username)
                _roomError.value = null
                loadRoom(code)
            } catch (e: Exception) {
                _roomError.value = res.getString(R.string.error_leaving_the_room, e.message)
            }
        }
    }

    fun loadRoom(code: String) {
        viewModelScope.launch {
            try {
                _roomData.value = roomRepo.getRoomWithUsers(code)
                _roomError.value = null
            } catch (e: Exception) {
                _roomError.value = res.getString(R.string.error_during_loading_room, e.message)
            }
        }
    }

    fun getRoomsByUser(username: String) {
        viewModelScope.launch {
            try {
                _roomByUsers.value = roomRepo.getRoomsByUser(username)
                _roomError.value = null
            } catch (e: Exception) {
                _roomError.value = res.getString(R.string.cannot_fetch_user_s_room, e.message)
            }
        }
    }

}