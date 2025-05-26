package it.namenotfoundexception.whats2watch.ui.theme.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.namenotfoundexception.whats2watch.R

enum class NavigationTab {
    HOME, ROOMS, REVIEWS
}

@Composable
fun BottomNavigationHomepage(
    modifier: Modifier = Modifier,
    activeTab: NavigationTab = NavigationTab.HOME,
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onReviewsClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationItem(
            icon = Icons.Default.Home,
            contentDescription = stringResource(R.string.home),
            isActive = activeTab == NavigationTab.HOME,
            onClick = onHomeClick
        )

        NavigationItem(
            icon = Icons.Default.Search,
            contentDescription = stringResource(R.string.rooms),
            isActive = activeTab == NavigationTab.ROOMS,
            onClick = onRoomsClick
        )

        NavigationItem(
            icon = Icons.Default.Star,
            contentDescription = stringResource(R.string.reviews),
            isActive = activeTab == NavigationTab.REVIEWS,
            onClick = onReviewsClick
        )
    }
}

@Composable
private fun NavigationItem(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
                if (isActive) Color.White else Color(0xFFE53935)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) Color(0xFFE53935) else Color.White
        )
    }
}