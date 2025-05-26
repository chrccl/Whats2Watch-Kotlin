package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.BottomNavigationHomepage
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.LoadingScreen
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.NavigationTab
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.RecentRoomsSection
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.SecondaryButton
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

@Composable
fun HomepageScreen(
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    roomViewModel: RoomViewModel = hiltViewModel<RoomViewModel>(),
    onLogoutClick: () -> Unit,
    onRoomsClick: (roomCode: String, username: String) -> Unit,
    onRoomMenuClick: () -> Unit,
    onReviewsClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)
    val currentUser by authViewModel.currentUser.collectAsState()
    val rooms by roomViewModel.roomByUsers.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            roomViewModel.getRoomsByUser(user.username)
        }
    }

    val user = currentUser

    if (user == null) {
        LoadingScreen(backgroundColor)
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            HomepageTopBar(
                username = user.username,
                onLogoutClick = onLogoutClick
            )

            RecentRoomsSection(
                rooms = rooms ?: emptyList(),
                onRoomClick = { room ->
                    onRoomsClick(room.code, user.username)
                },
                modifier = Modifier.weight(1f)
            )
        }

        CreateRoomFAB(
            onClick = {
                val code = generateRoomCode()
                roomViewModel.createRoom(code, user.username)
                onRoomsClick(code, user.username)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 16.dp)
        )

        BottomNavigationHomepage(
            modifier = Modifier.align(Alignment.BottomCenter),
            activeTab = NavigationTab.HOME,
            onRoomsClick = onRoomMenuClick,
            onReviewsClick = onReviewsClick
        )
    }
}

@Composable
private fun HomepageTopBar(
    username: String,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Whats2Watch",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Welcome, $username",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        SecondaryButton(
            text = "Logout",
            onClick = onLogoutClick
        )
    }
}

@Composable
private fun CreateRoomFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFE53935),
        contentColor = Color.White,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Room"
        )
    }
}

private fun generateRoomCode(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..6)
        .map { chars.random() }
        .joinToString("")
}