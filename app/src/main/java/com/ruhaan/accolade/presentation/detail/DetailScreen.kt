package com.ruhaan.accolade.presentation.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ruhaan.accolade.domain.model.Genre
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.domain.model.MovieDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    movieId: Int,
    mediaType: MediaType,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
  val uiState by viewModel.detailState.collectAsState()

  LaunchedEffect(movieId, mediaType) { viewModel.loadDetail(movieId, mediaType) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Details") },
            navigationIcon = {
              IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
        )
      }
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      when (val state = uiState) {
        is DetailUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is DetailUiState.Error -> {
          ErrorView(
              message = state.message,
              onRetry = { viewModel.loadDetail(movieId, mediaType) },
              modifier = Modifier.align(Alignment.Center),
          )
        }
        is DetailUiState.Success -> {
          DetailContent(
              detail = state.detail,
              onCastClick = {
                navController.navigate("cast/${state.detail.id}/${state.detail.mediaType.name}")
              },
              onCrewClick = {
                navController.navigate("crew/${state.detail.id}/${state.detail.mediaType.name}")
              },
              onGenreClick = { genre ->
                navController.navigate("category/${genre.id}/${genre.name}")
              },
          )
        }
      }
    }
  }
}

@Composable
private fun DetailContent(
    detail: MovieDetail,
    onCastClick: () -> Unit,
    onCrewClick: () -> Unit,
    onGenreClick: (Genre) -> Unit,
) {
  val scrollState = rememberScrollState()

  Column(
      modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Poster
    PosterSection(posterPath = detail.posterPath)

    // Title
    Text(
        text = detail.title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )

    // Meta Information
    MetaInfoSection(detail = detail)

    // ADD THIS - Genre chips section
    if (detail.genres.isNotEmpty()) {
      GenreChipsSection(genres = detail.genres, onGenreClick = onGenreClick)
    }

    // Synopsis
    SynopsisSection(synopsis = detail.synopsis)

    // Trailer (only if available)
    detail.trailer?.let { trailer -> TrailerSection(trailer = trailer) }

    // Rating
    RatingSection(rating = detail.rating)

    // Cast & Crew Buttons
    CastCrewButtons(onCastClick = onCastClick, onCrewClick = onCrewClick)
  }
}

@Composable
private fun PosterSection(posterPath: String) {
  val context = LocalContext.current

  SubcomposeAsyncImage(
      model = ImageRequest.Builder(context).data(posterPath).crossfade(true).build(),
      contentDescription = "Movie Poster",
      modifier = Modifier.fillMaxWidth().height(400.dp).clip(RoundedCornerShape(12.dp)),
      contentScale = ContentScale.Crop,
      loading = {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      },
      error = {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
          Text("Failed to load poster")
        }
      },
  )
}

// ADD THIS NEW COMPOSABLE
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreChipsSection(genres: List<Genre>, onGenreClick: (Genre) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
        text = "Genres",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      genres.forEach { genre ->
        FilterChip(
            selected = false,
            onClick = { onGenreClick(genre) },
            label = { Text(genre.name) },
            colors =
                FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        )
      }
    }
  }
}

@Composable
private fun MetaInfoSection(detail: MovieDetail) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    MetaInfoRow(
        label = "Type",
        value = if (detail.mediaType == MediaType.MOVIE) "Movie" else "TV Show",
    )
    MetaInfoRow(label = "Country", value = detail.country)
    MetaInfoRow(label = "Language", value = detail.language)
    MetaInfoRow(
        label = if (detail.mediaType == MediaType.MOVIE) "Director" else "Showrunner",
        value = detail.directorOrShowrunner,
    )
    MetaInfoRow(
        label = if (detail.mediaType == MediaType.MOVIE) "Runtime" else "Avg. Episode Length",
        value = detail.runtime,
    )
  }
}

@Composable
private fun MetaInfoRow(label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontWeight = FontWeight.Medium,
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Normal,
    )
  }
}

@Composable
private fun SynopsisSection(synopsis: String) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
        text = "Synopsis",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
        text = synopsis,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        lineHeight = 20.sp,
    )
  }
}

@Composable
private fun TrailerSection(trailer: com.ruhaan.accolade.domain.model.Trailer) {
  val context = LocalContext.current

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
        text = "Trailer",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )

    Box(
        modifier =
            Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).clickable {
              val intent =
                  Intent(
                      Intent.ACTION_VIEW,
                      "https://www.youtube.com/watch?v=${trailer.key}".toUri(),
                  )
              context.startActivity(intent)
            }
    ) {
      // Trailer thumbnail
      SubcomposeAsyncImage(
          model = ImageRequest.Builder(context).data(trailer.thumbnailUrl).crossfade(true).build(),
          contentDescription = "Trailer Thumbnail",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          loading = {
            Box(
                modifier =
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator()
            }
          },
      )

      // Play button overlay
      Box(
          modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
          contentAlignment = Alignment.Center,
      ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play Trailer",
            modifier = Modifier.size(64.dp),
            tint = Color.White,
        )
      }
    }

    Text(
        text = trailer.name,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    )
  }
}

@Composable
private fun RatingSection(rating: Int) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = "TMDB Rating",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
        text = "$rating%",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color =
            when {
              rating >= 70 -> Color(0xFF4CAF50) // Green
              rating >= 50 -> Color(0xFFFFC107) // Yellow
              else -> Color(0xFFF44336) // Red
            },
    )
  }
}

@Composable
private fun CastCrewButtons(onCastClick: () -> Unit, onCrewClick: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    Button(
        onClick = onCastClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
      Text("View Cast")
    }

    Button(
        onClick = onCrewClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
      Text("View Crew")
    }
  }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
        text = "Error",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.error,
    )
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
    )
    Button(onClick = onRetry) { Text("Retry") }
  }
}
