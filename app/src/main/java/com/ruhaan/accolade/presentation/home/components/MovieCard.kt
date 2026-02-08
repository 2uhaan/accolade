package com.ruhaan.accolade.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ruhaan.accolade.R
import com.ruhaan.accolade.domain.model.Movie
import com.ruhaan.accolade.ui.theme.SkyBlue

@Composable
fun MovieCard(movie: Movie, onMovieClick: (Movie) -> Unit, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // Subtle background highlight on click
  val backgroundColor by
      animateColorAsState(
          targetValue = if (isPressed) Color(0xFF2A2A2A) else Color.Transparent,
          animationSpec = tween(durationMillis = 100),
          label = "background_color",
      )

  // Build complete image URL
  val imageUrl = remember(movie.posterPath) { "https://image.tmdb.org/t/p/w500${movie.posterPath}" }

  Column(
      modifier =
          modifier
              .fillMaxWidth()
              .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
              .clickable(interactionSource = interactionSource, indication = null) {
                onMovieClick(movie)
              }
              .padding(8.dp)
              .semantics {
                contentDescription = "${movie.title}, ${movie.year}"
                role = Role.Button
              }
  ) {
    // Poster Image
    SubcomposeAsyncImage(
        model =
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .memoryCacheKey(imageUrl) // Cache optimization
                .diskCacheKey(imageUrl)
                .build(),
        contentDescription = "Poster for ${movie.title}",
        modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
        loading = { LoadingPlaceholder() },
        error = { ErrorPlaceholder() },
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Movie Info
    MovieInfo(movie = movie)
  }
}

@Composable
private fun LoadingPlaceholder() {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(
                  brush =
                      Brush.verticalGradient(colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)))
              ),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator(
        modifier = Modifier.size(32.dp),
        color = SkyBlue,
        strokeWidth = 3.dp,
    )
  }
}

@Composable
private fun ErrorPlaceholder() {
  Box(
      modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE)),
      contentAlignment = Alignment.Center,
  ) {
    Image(
        painter = painterResource(id = R.drawable.error),
        contentDescription = "Failed to load poster",
        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
  }
}

@Composable
private fun MovieInfo(movie: Movie) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    Text(
        text = movie.title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onBackground,
    )

    Text(
        text = movie.year,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontWeight = FontWeight.Normal,
    )
  }
}
