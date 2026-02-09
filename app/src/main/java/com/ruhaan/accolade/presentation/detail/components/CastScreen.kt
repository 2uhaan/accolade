package com.ruhaan.accolade.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ruhaan.accolade.domain.model.CastMember
import com.ruhaan.accolade.domain.model.MediaType
import com.ruhaan.accolade.presentation.detail.CastCrewUiState
import com.ruhaan.accolade.presentation.detail.MovieDetailViewModel
import com.ruhaan.accolade.presentation.detail.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastScreen(
    navController: NavController,
    movieId: Int,
    mediaType: MediaType,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.castCrewState.collectAsState()

    LaunchedEffect(movieId, mediaType) {
        viewModel.loadCast(movieId, mediaType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cast") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is CastCrewUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CastCrewUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadCast(movieId, mediaType) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CastCrewUiState.CastSuccess -> {
                    CastList(cast = state.cast)
                }
                else -> {
                    // Should not happen
                }
            }
        }
    }
}

@Composable
private fun CastList(cast: List<CastMember>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cast) { member ->
            CastItem(member = member)
        }
    }
}

@Composable
private fun CastItem(member: CastMember) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            if (member.profilePath != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(member.profilePath)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile of ${member.name}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    },
                    error = {
                        PlaceholderProfile()
                    }
                )
            } else {
                PlaceholderProfile()
            }

            // Name and Character
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = member.character,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PlaceholderProfile() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "No profile picture",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}