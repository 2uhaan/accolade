package com.ruhaan.accolade.presentation.detail

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())) {
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
                            navController.navigate(
                                "castcrew/${state.detail.id}/${state.detail.mediaType.name}/cast"
                            )
                        },
                        onCrewClick = {
                            navController.navigate(
                                "castcrew/${state.detail.id}/${state.detail.mediaType.name}/crew"
                            )
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Poster ──────────────────────────────────────────────────
        PosterSection(
            posterPath = detail.posterPath,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 16.dp),
        )

        // ── Title ───────────────────────────────────────────────────
        Text(
            text = detail.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Meta row  (Country · Language · TYPE) ───────────────────
        MetaRow(detail = detail)

        Spacer(modifier = Modifier.height(12.dp))

        // ── Genre chips ─────────────────────────────────────────────
        if (detail.genres.isNotEmpty()) {
            GenreChipsSection(genres = detail.genres, onGenreClick = onGenreClick)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Circular rating ─────────────────────────────────────────
        CircularRatingSection(rating = detail.rating)

        Spacer(modifier = Modifier.height(28.dp))

        // ── About / Synopsis ────────────────────────────────────────
        AboutSection(
            synopsis = detail.synopsis,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Cast & Crew cards ───────────────────────────────────────
        CastCrewCards(
            onCastClick = onCastClick,
            onCrewClick = onCrewClick,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        // ── Trailer ─────────────────────────────────────────────────
        detail.trailer?.let { trailer ->
            Spacer(modifier = Modifier.height(28.dp))
            TrailerSection(
                trailer = trailer,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Poster
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun PosterSection(posterPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context).data(posterPath).crossfade(true).build(),
        contentDescription = "Movie Poster",
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) { Text("Failed to load poster") }
        },
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Meta row  –  "USA · English · MOVIE"
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun MetaRow(detail: MovieDetail) {
    val typeLabel = if (detail.mediaType == MediaType.MOVIE) "MOVIE" else "TV SHOW"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = detail.country,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        DotSeparator()
        Text(
            text = detail.language,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        DotSeparator()
        Text(
            text = typeLabel,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun DotSeparator() {
    Text(
        text = " · ",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Genre chips
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreChipsSection(genres: List<Genre>, onGenreClick: (Genre) -> Unit) {
    FlowRow(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        genres.forEach { genre ->
            SuggestionChip(
                onClick = { onGenreClick(genre) },
                label = {
                    Text(
                        text = genre.name.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                ),
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Circular rating
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CircularRatingSection(rating: Int) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = rating / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "ratingAnim",
    )

    LaunchedEffect(rating) { animatedProgress = rating / 100f }

    val ringColor = when {
        rating >= 70 -> Color(0xFF4CAF50)
        rating >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Box(contentAlignment = Alignment.Center) {
        // Background ring
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(120.dp),
            color = ringColor.copy(alpha = 0.15f),
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
        )
        // Foreground ring
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(120.dp),
            color = ringColor,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
        )
        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$rating%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "MATCH SCORE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 0.8.sp,
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// About / Synopsis
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun AboutSection(synopsis: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        // Left accent border
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "ABOUT",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp,
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = synopsis,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        lineHeight = 22.sp,
        modifier = modifier,
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Cast & Crew cards
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CastCrewCards(
    onCastClick: () -> Unit,
    onCrewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CastCrewCard(
            label = "CAST",
            onClick = onCastClick,
            modifier = Modifier.weight(1f),
        )
        CastCrewCard(
            label = "CREW",
            onClick = onCrewClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CastCrewCard(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Trailer section
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrailerSection(
    trailer: com.ruhaan.accolade.domain.model.Trailer,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Header row with "WATCH TRAILER" and "ALL CLIPS"
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "WATCH TRAILER",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = "ALL CLIPS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.clickable { /* navigate to clips */ },
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Thumbnail + play overlay
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://www.youtube.com/watch?v=${trailer.key}".toUri(),
                )
                context.startActivity(intent)
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context).data(trailer.thumbnailUrl).crossfade(true).build(),
            contentDescription = "Trailer Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            },
        )

        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        // Play button
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.Center)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Trailer",
                modifier = Modifier.size(34.dp),
                tint = Color.Black,
            )
        }

        // Clip title label at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = trailer.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Error view
// ────────────────────────────────────────────────────────────────────────────

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