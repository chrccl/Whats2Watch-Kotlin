package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.User
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

@Composable
fun RoomsScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onJoinRoomSuccess: (String, String) -> Unit,
    onHomeClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)

    var roomCode by remember { mutableStateOf("") }
    var isJoining by remember { mutableStateOf(false) }
    var joinError by remember { mutableStateOf<String?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val roomData by roomViewModel.roomData.collectAsState()
    val roomError by roomViewModel.roomError.collectAsState()

    // Osserva se il join è riuscito
    LaunchedEffect(roomData) {
        if (roomData != null && isJoining) {
            currentUser?.let { user ->
                isJoining = false
                onJoinRoomSuccess(roomCode, user.username)
            }
        }
    }

    // Osserva errori della room
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
                    currentUser?.let { user ->
                        Text(
                            text = "Welcome, ${user.username}",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Logout")
                }
            }

            // Main content area with join room card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Join Room Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Join in a Room",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Mostra errore se presente
                        val errorToShow = joinError ?: roomError
                        errorToShow?.let { error ->
                            Text(
                                text = error,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        OutlinedTextField(
                            value = roomCode,
                            onValueChange = {
                                roomCode = it
                                joinError = null // Reset error when typing
                            },
                            label = { Text("Room Code", color = Color.White) },
                            singleLine = true,
                            enabled = !isJoining,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = {
                                if (roomCode.isNotEmpty() && currentUser != null) {
                                    val user: User = currentUser!!;
                                    isJoining = true
                                    joinError = null
                                    roomViewModel.joinRoom(roomCode.trim(), user.username)
                                } else if (currentUser == null) {
                                    joinError = "User not logged in"
                                } else {
                                    joinError = "Please enter a room code"
                                }
                            },
                            enabled = !isJoining && roomCode.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (isJoining) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Join",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation
        BottomNavigationRooms(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = onHomeClick
        )
    }
}

@Composable
fun BottomNavigationRooms(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935))
                .clickable { onHomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White
            )
        }

        // Rooms Icon (Active)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rooms",
                tint = Color(0xFFE53935)
            )
        }
    }
}

@Preview(showBackground = true, name = "Rooms Screen Preview")
@Composable
fun RoomsScreenPreview() {
    RoomsScreen(
        modifier = Modifier,
        onLogoutClick = { /* no-op for preview */ },
        onJoinRoomSuccess = { roomCode, username ->
            println("Join room: $roomCode for user: $username")
        },
        onHomeClick = { /* no-op for preview */ }
    )
}