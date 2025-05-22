package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RoomsScreen(
    //navController: NavController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onJoinRoomClick: (String) -> Unit,
    onCreateRoomClick: (String, String, String, String, String) -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)

    var roomCode by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var watchProviders by remember { mutableStateOf("") }
    var productionCompanies by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf("") }

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
                Text(
                    text = "Whats2Watch",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

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

            // Main content area with two cards
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

                        OutlinedTextField(
                            value = roomCode,
                            onValueChange = { roomCode = it },
                            label = { Text("Code", color = Color.White) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor         = Color.White,
                                unfocusedTextColor       = Color.White,
                                focusedContainerColor    = Color.Transparent,
                                unfocusedContainerColor  = Color.Transparent,
                                focusedIndicatorColor    = Color.White,
                                unfocusedIndicatorColor  = Color.Gray,
                                cursorColor              = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = { onJoinRoomClick(roomCode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Join",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation
        BottomNavigationRooms(
            modifier = Modifier.align(Alignment.BottomCenter),
            //navController = navController
        )
    }
}

@Composable
fun BottomNavigationRooms(
    modifier: Modifier = Modifier
    //navController: NavController
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
                .background(Color(0xFFE53935)),
            //.clickable { navController.navigate("home") },
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
            //.clickable { navController.navigate("rooms") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rooms",
                tint = Color(0xFFE53935)
            )
        }

// Profile Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935)),
            //.clickable { navController.navigate("profile") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White
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
        onJoinRoomClick = { roomCode -> println("Join room: $roomCode") },
        onCreateRoomClick = { roomName, hostUsername, genre, actor, director ->
            println("Create room with: $roomName, $hostUsername, $genre, $actor, $director")
        }
    )
}