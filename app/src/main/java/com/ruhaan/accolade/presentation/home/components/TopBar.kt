package com.ruhaan.accolade.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ModernTopBar(
    isSearchExpanded: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSearchClose: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope, // ADD
    animatedVisibilityScope: AnimatedVisibilityScope, // ADD
) {
  with(sharedTransitionScope) {
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
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                )
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp, bottom = 25.dp)
    ) {
      AnimatedVisibility(
          visible = !isSearchExpanded,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically(),
      ) {
        // Collapsed State - Original Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
              text = "Accolade",
              fontSize = 25.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White,
          )

          Box(
              modifier =
                  Modifier.size(45.dp)
                      .sharedElement(
                          rememberSharedContentState(key = "search_bar"),
                          animatedVisibilityScope = animatedVisibilityScope,
                      )
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

      AnimatedVisibility(
          visible = isSearchExpanded,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically(),
      ) {
        // Expanded State - Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          // Close button
          IconButton(onClick = onSearchClose, modifier = Modifier.size(45.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Search",
                tint = Color.White,
                modifier = Modifier.size(25.dp),
            )
          }

          // Search input field
          TextField(
              value = searchQuery,
              onValueChange = onSearchQueryChange,
              modifier = Modifier.weight(1f),
              placeholder = {
                Text("Search movies & TV shows...", color = Color.White.copy(alpha = 0.7f))
              },
              colors =
                  TextFieldDefaults.colors(
                      focusedContainerColor = Color.White.copy(alpha = 0.2f),
                      unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White,
                      cursorColor = Color.White,
                      focusedIndicatorColor = Color.Transparent,
                      unfocusedIndicatorColor = Color.Transparent,
                  ),
              shape = RoundedCornerShape(16.dp),
              singleLine = true,
          )
        }
      }
    }
  }
}
