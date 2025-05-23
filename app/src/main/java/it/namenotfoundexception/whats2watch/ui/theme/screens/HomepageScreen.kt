package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    roomViewModel: RoomViewModel = hiltViewModel<RoomViewModel>(),
    onLogoutClick: () -> Unit,
    onRoomsClick: (roomCode: String, username: String) -> Unit,
    onRoomMenuClick: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    roomViewModel.getRoomsByUser(currentUser!!.username)
    val rooms by roomViewModel.roomByUsers.collectAsState()

    val bottomNavItems = remember {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, selected = true),
            BottomNavItem("Rooms", Icons.Default.PlayArrow),
            BottomNavItem("Profile", Icons.Default.Person)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Whats2Watch",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        currentUser?.let { user ->
                            Text(
                                text = "Welcome, ${user.username}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Logout", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2D3748)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                onItemClick = { item ->
                    when (item.title) {
                        "Rooms" -> onRoomMenuClick() //da cambiare
                        "Home" -> fun() {}
                    }
                }
            )
        },
        floatingActionButton = {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            FloatingActionButton(
                onClick = {
                    val code = (1..6)
                        .map { chars.random() }
                        .joinToString("")
                    roomViewModel.createRoom(code, currentUser!!.username)
                    onRoomsClick(code, currentUser!!.username)
                },
                containerColor = Color(0xFFE53E3E),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Room"
                )
            }
        },
        containerColor = Color(0xFF1A202C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1A202C))
        ) {
            RecentRoomsSection(
                rooms = rooms ?: emptyList(),
                onRoomClick = { room ->
                    onRoomsClick(room.code, currentUser!!.username)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D3748)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            AsyncImage(
//                model = room.,
//                contentDescription = room.name,
//                modifier = Modifier
//                    .size(96.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Crop
//            )

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
                        text = "${roomWithUser!!.participants.size} Members",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF2D3748),
        modifier = Modifier.height(64.dp)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (item.selected) Color(0xFFE53E3E) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = if (item.selected) Color(0xFFE53E3E) else Color.Gray
                    )
                },
                selected = item.selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE53E3E),
                    selectedTextColor = Color(0xFFE53E3E),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
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