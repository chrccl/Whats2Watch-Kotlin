package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.User
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppTitle
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.BottomNavigationHomepage
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.NavigationTab
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.TopBar
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.joinRoomCard
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

@Composable
fun RoomsScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onJoinRoomSuccess: (String, String) -> Unit,
    onHomeClick: () -> Unit,
    onReviewsClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)

    var roomCode by remember { mutableStateOf("") }
    var isJoining by remember { mutableStateOf(false) }
    var joinError by remember { mutableStateOf<String?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val roomData by roomViewModel.roomData.collectAsState()
    val roomError by roomViewModel.roomError.collectAsState()

    // Observe successful join
    LaunchedEffect(roomData) {
        if (roomData != null && isJoining) {
            currentUser?.let { user ->
                isJoining = false
                onJoinRoomSuccess(roomCode, user.username)
            }
        }
    }

    // Observe room errors
    LaunchedEffect(roomError) {
        if (roomError != null && isJoining) {
            joinError = roomError
            isJoining = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom navigation
        ) {
            // Top Bar
            TopBar(
                title = { AppTitle() },
                subtitle = currentUser?.let { "Welcome, ${it.username}" },
                onLogoutClick = onLogoutClick,
                modifier = Modifier.padding(16.dp)
            )

            // Main content area with join room card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                joinRoomCard(
                    roomCode = roomCode,
                    onRoomCodeChange = {
                        roomCode = it
                        joinError = null
                    },
                    isJoining = isJoining,
                    error = joinError ?: roomError,
                    onJoinClick = {
                        if (roomCode.isNotEmpty() && currentUser != null) {
                            val user: User = currentUser!!
                            isJoining = true
                            joinError = null
                            roomViewModel.joinRoom(roomCode.trim(), user.username)
                        } else if (currentUser == null) {
                            joinError = "User not logged in"
                        } else {
                            joinError = "Please enter a room code"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        // Bottom Navigation
        BottomNavigationHomepage(
            modifier = Modifier.align(Alignment.BottomCenter),
            activeTab = NavigationTab.ROOMS,
            onHomeClick = onHomeClick,
            onReviewsClick = onReviewsClick
        )
    }
}
