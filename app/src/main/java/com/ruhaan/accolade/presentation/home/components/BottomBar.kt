package com.ruhaan.accolade.presentation.home.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ruhaan.accolade.R

sealed class BottomNavItem(
    val route: String,
    val icon: Int
) {
    object Movies : BottomNavItem("home", R.drawable.ic_home) // or ic_list
    object Schedule : BottomNavItem("schedule", R.drawable.ic_schedule) // or ic_message
    object Profile : BottomNavItem("profile", R.drawable.ic_pick) // or ic_person
}

@Composable
fun FloatingBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Movies,
        BottomNavItem.Schedule,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavIcon(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    val backgroundColor = if (isSelected) {
        Color(0xFF2196F3) // Blue color from your reference
    } else {
        Color.Transparent
    }

    val iconTint = if (isSelected) {
        Color.White
    } else {
        Color(0xFF9E9E9E) // Gray for unselected
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .size(56.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = ripple(bounded = true, radius = 28.dp),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.route,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
