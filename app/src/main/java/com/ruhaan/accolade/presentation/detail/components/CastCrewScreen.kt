package com.ruhaan.accolade.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.presentation.common.AppSpacing
import com.ruhaan.accolade.presentation.detail.CastCrewUiState
import com.ruhaan.accolade.presentation.detail.ErrorView
import com.ruhaan.accolade.presentation.detail.MovieDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastCrewScreen(
    navController: NavController,
    movieId: Int,
    mediaType: MediaType,
    screenType: CastCrewScreenType,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
  val uiState by viewModel.castCrewState.collectAsState()

  LaunchedEffect(movieId, mediaType, screenType) {
    when (screenType) {
      CastCrewScreenType.CAST -> viewModel.loadCast(movieId, mediaType)
      CastCrewScreenType.CREW -> viewModel.loadCrew(movieId, mediaType)
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(screenType.title, fontWeight = FontWeight.Bold)
              }
            },
            navigationIcon = {
              IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            },
            actions = { Spacer(modifier = Modifier.width(48.dp)) },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
        )
      }
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())) {
      when (val state = uiState) {
        is CastCrewUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is CastCrewUiState.Error -> {
          ErrorView(
              message = state.message,
              onRetry = {
                when (screenType) {
                  CastCrewScreenType.CAST -> viewModel.loadCast(movieId, mediaType)
                  CastCrewScreenType.CREW -> viewModel.loadCrew(movieId, mediaType)
                }
              },
              modifier = Modifier.align(Alignment.Center),
          )
        }
        is CastCrewUiState.CastSuccess -> {
          if (screenType == CastCrewScreenType.CAST) {
            PersonList(
                items = state.cast,
                getName = { it.name },
                getProfilePath = { it.profilePath },
                getSubtitle = { it.character },
            )
          }
        }
        is CastCrewUiState.CrewSuccess -> {
          if (screenType == CastCrewScreenType.CREW) {
            PersonList(
                items = state.crew,
                getName = { it.name },
                getProfilePath = { it.profilePath },
                getSubtitle = { it.job },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun <T> PersonList(
    items: List<T>,
    getName: (T) -> String,
    getProfilePath: (T) -> String?,
    getSubtitle: (T) -> String,
) {
  LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      contentPadding =
          PaddingValues(
              start = 16.dp,
              end = 16.dp,
              top = 16.dp,
              bottom = AppSpacing.contentPaddingBottom,
          ),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    items(items) { item ->
      PersonItem(
          name = getName(item),
          profilePath = getProfilePath(item),
          subtitle = getSubtitle(item),
      )
    }
  }
}

@Composable
private fun PersonItem(name: String, profilePath: String?, subtitle: String) {
  val context = LocalContext.current

  Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // Profile Picture (Large Circle)
    if (profilePath != null) {
      SubcomposeAsyncImage(
          model = ImageRequest.Builder(context).data(profilePath).crossfade(true).build(),
          contentDescription = "Profile of $name",
          modifier = Modifier.size(120.dp).clip(CircleShape),
          contentScale = ContentScale.Crop,
          loading = {
            Box(
                modifier =
                    Modifier.size(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
          },
          error = { PlaceholderProfile(size = 120.dp, iconSize = 60.dp) },
      )
    } else {
      PlaceholderProfile(size = 120.dp, iconSize = 60.dp)
    }

    // Name
    Text(
        text = name,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )

    // Subtitle (Character or Job)
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun PlaceholderProfile(size: Dp = 64.dp, iconSize: Dp = 32.dp) {
  Box(
      modifier =
          Modifier.size(size).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "No profile picture",
        modifier = Modifier.size(iconSize),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

enum class CastCrewScreenType(val title: String) {
  CAST("Cast"),
  CREW("Crew"),
}
