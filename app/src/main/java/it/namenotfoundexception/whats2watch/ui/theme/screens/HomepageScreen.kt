package it.namenotfoundexception.whats2watch.ui.theme.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class Room(
    val id: String,
    val name: String,
    val memberCount: Int,
    val genre: String,
    val imageUrl: String
)

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Whats2WatchHomepage() {
    val rooms = remember {
        listOf(
            Room("1", "Horror Night", 8, "Horror", "https://via.placeholder.com/200x300/8B0000/FFFFFF?text=HORROR"),
            Room("2", "Action Heroes", 12, "Action", "https://via.placeholder.com/200x300/FF4500/FFFFFF?text=ACTION"),
            Room("3", "Sci-Fi Space", 6, "Sci-Fi", "https://via.placeholder.com/200x300/4682B4/FFFFFF?text=SCI-FI"),
            Room("4", "Comedy Corner", 15, "Comedy", "https://via.placeholder.com/200x300/FFD700/000000?text=COMEDY"),
            Room("5", "Drama Club", 9, "Drama", "https://via.placeholder.com/200x300/800080/FFFFFF?text=DRAMA")
        )
    }

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
                    Text(
                        text = "Whats2Watch",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    Button(
                        onClick = { /* Logout action */ },
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
                onItemClick = { /* Handle navigation */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Create new room */ },
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
                rooms = rooms,
                onRoomClick = { room -> /* Handle room click */ },
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
    onClick: () -> Unit
) {
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
            AsyncImage(
                model = room.imageUrl,
                contentDescription = room.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = room.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Column {
                    Text(
                        text = "${room.memberCount} Members",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${room.genre} Room",
                        fontSize = 12.sp,
                        color = Color(0xFFE53E3E)
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
@Preview(showBackground = true)
@Composable
fun Whats2WatchHomepagePreview() {
    MaterialTheme {
        Whats2WatchHomepage()
    }
}