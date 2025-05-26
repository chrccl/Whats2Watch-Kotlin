package it.namenotfoundexception.whats2watch.ui.theme.screens.common

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.viewmodels.RoomViewModel

@Composable
fun RecentRoomsSection(
    rooms: List<Room>,
    onRoomClick: (Room) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.Spacing.dp)
    ) {
        SectionTitle(
            text = "Recent Rooms",
            modifier = Modifier.padding(vertical = AppDimensions.Spacing.dp)
        )

        if (rooms.isNotEmpty()) {
            RoomsList(
                rooms = rooms,
                onRoomClick = onRoomClick
            )
        } else {
            EmptyRoomsMessage()
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = AppTextSizes.Subtitle.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.OnBackground,
        modifier = modifier
    )
}

@Composable
private fun RoomsList(
    rooms: List<Room>,
    onRoomClick: (Room) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = AppDimensions.Spacing.dp)
    ) {
        items(rooms) { room ->
            RoomCard(
                room = room,
                onClick = { onRoomClick(room) }
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
    val roomWithUser by roomViewModel.roomData.collectAsState()

    LaunchedEffect(room.code) {
        roomViewModel.loadRoom(room.code)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation.dp)
    ) {
        RoomCardContent(
            room = room,
            memberCount = roomWithUser?.participants?.size ?: 0
        )
    }
}

@Composable
private fun RoomCardContent(
    room: Room,
    memberCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(AppDimensions.Spacing.dp))

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = room.code,
                fontSize = AppTextSizes.Caption.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "$memberCount Members",
                fontSize = AppTextSizes.Small.sp,
                color = AppColors.Secondary
            )
        }
    }
}

@Composable
private fun EmptyRoomsMessage() {
    Text(
        text = "No rooms found",
        fontSize = AppTextSizes.Small.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.OnBackground,
        modifier = Modifier.padding(vertical = AppDimensions.Spacing.dp)
    )
}
@Composable
fun joinRoomCard(
    roomCode: String,
    onRoomCodeChange: (String) -> Unit,
    isJoining: Boolean,
    error: String?,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                text = "Join a Room",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Show error if present
            error?.let { errorMessage ->
                ErrorText(
                    error = errorMessage,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            roomCodeTextField(
                value = roomCode,
                onValueChange = onRoomCodeChange,
                enabled = !isJoining,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            joinButton(
                onClick = onJoinClick,
                enabled = !isJoining && roomCode.isNotEmpty(),
                isLoading = isJoining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

@Composable
private fun roomCodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Room Code", color = Color.White) },
        singleLine = true,
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.Gray,
            cursorColor = Color.White
        ),
        modifier = modifier
    )
}

@Composable
private fun joinButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE53935)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        if (isLoading) {
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