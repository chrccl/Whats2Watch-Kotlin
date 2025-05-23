package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

@Composable
fun HomepageScreen(
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    roomViewModel: RoomViewModel = hiltViewModel<RoomViewModel>(),
    onLogoutClick: () -> Unit,
    onRoomsClick: (roomCode: String, username: String) -> Unit,
    onRoomMenuClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)
    val currentUser by authViewModel.currentUser.collectAsState()
    roomViewModel.getRoomsByUser(currentUser!!.username)
    val rooms by roomViewModel.roomByUsers.collectAsState()

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

            // Main content
            RecentRoomsSection(
                rooms = rooms ?: emptyList(),
                onRoomClick = { room ->
                    onRoomsClick(room.code, currentUser!!.username)
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                val code = (1..6)
                    .map { chars.random() }
                    .joinToString("")
                roomViewModel.createRoom(code, currentUser!!.username)
                onRoomsClick(code, currentUser!!.username)
            },
            containerColor = Color(0xFFE53935),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Room"
            )
        }

        // Bottom Navigation
        BottomNavigationHomepage(
            modifier = Modifier.align(Alignment.BottomCenter),
            onRoomsClick = onRoomMenuClick
        )
    }
}

@Composable
fun RecentRoomsSection(
    rooms: List<Room>,
    onRoomClick: (Room) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recent Rooms",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if(rooms.size > 0){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(rooms) { room ->
                    RoomCard(
                        room = room,
                        onClick = { onRoomClick(room) }
                    )
                }
            }
        }else{
            Text(
                text = "No rooms founded",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun RoomCard(
    room: Room,
    onClick: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel<RoomViewModel>()
) {
    roomViewModel.loadRoom(room.code)
    val roomWithUser by roomViewModel.roomData.collectAsState()

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = room.code,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Column {
                    Text(
                        text = "${roomWithUser?.participants?.size ?: 0} Members",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationHomepage(
    modifier: Modifier = Modifier,
    onRoomsClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Icon (Active)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color(0xFFE53935)
            )
        }

        // Rooms Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935))
                .clickable { onRoomsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rooms",
                tint = Color.White
            )
        }
    }
}

// Preview
//@Preview(showBackground = true)
//@Composable
//fun HomepageScreenPreview() {
//    MaterialTheme {
//        HomepageScreen(
//            onLogoutClick = { /* no-op for preview */ },
//            onRoomsClick = { /* no-op for preview */ },
//            onProfileClick = {}
//            onR
//        )
//    }
//}