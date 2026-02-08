package com.ruhaan.accolade.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernTopBar(onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier
              .fillMaxWidth()
              .shadow(
                  elevation = 8.dp,
                  shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                  spotColor = Color.Black.copy(alpha = 0.15f),
              )
              .background(
                  color = Color(0xFF2196F3),
                  shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
              )
              .padding(horizontal = 20.dp)
              .padding(top = 60.dp, bottom = 25.dp) // Extra top padding for status bar
  ) {
    // App Name on the left
    Text(
        text = "Accolade",
        fontSize = 25.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier.align(Alignment.CenterStart),
    )

    // Search icon in a circular background on the right
    Box(
        modifier =
            Modifier.align(Alignment.CenterEnd)
                .size(45.dp)
                .background(color = Color.White.copy(alpha = 0.3f), shape = CircleShape)
                .clickable(
                    onClick = onSearchClick,
                    indication = ripple(bounded = true, radius = 20.dp),
                    interactionSource = remember { MutableInteractionSource() },
                ),
        contentAlignment = Alignment.Center,
    ) {
      Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = Color.White,
          modifier = Modifier.size(25.dp),
      )
    }
  }
}
