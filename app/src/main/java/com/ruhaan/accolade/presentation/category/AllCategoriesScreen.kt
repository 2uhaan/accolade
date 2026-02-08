package com.ruhaan.accolade.presentation.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ruhaan.accolade.domain.model.Genre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCategoriesScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Categories") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getAllGenres()) { genre ->
                CategoryCard(
                    genre = genre,
                    onClick = {
                        navController.navigate("category/${genre.id}/${genre.name}")
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    genre: Genre,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = getGenreColor(genre.id)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// All available genres (merged from movies and TV)
private fun getAllGenres(): List<Genre> {
    return listOf(
        Genre(28, "Action"),
        Genre(12, "Adventure"),
        Genre(16, "Animation"),
        Genre(35, "Comedy"),
        Genre(80, "Crime"),
        Genre(99, "Documentary"),
        Genre(18, "Drama"),
        Genre(10751, "Family"),
        Genre(14, "Fantasy"),
        Genre(36, "History"),
        Genre(27, "Horror"),
        Genre(10402, "Music"),
        Genre(9648, "Mystery"),
        Genre(10749, "Romance"),
        Genre(878, "Science Fiction"),
        Genre(53, "Thriller"),
        Genre(10752, "War"),
        Genre(37, "Western"),
        Genre(10759, "Action & Adventure"),
        Genre(10762, "Kids"),
        Genre(10763, "News"),
        Genre(10764, "Reality"),
        Genre(10765, "Sci-Fi & Fantasy"),
        Genre(10766, "Soap"),
        Genre(10767, "Talk"),
        Genre(10768, "War & Politics"),
    )
}

// Assign unique colors to each genre
private fun getGenreColor(genreId: Int): Color {
    return when (genreId) {
        28 -> Color(0xFFE53935) // Action - Red
        12 -> Color(0xFFFB8C00) // Adventure - Orange
        16 -> Color(0xFFAB47BC) // Animation - Purple
        35 -> Color(0xFFFFEB3B) // Comedy - Yellow
        80 -> Color(0xFF424242) // Crime - Dark Gray
        99 -> Color(0xFF8D6E63) // Documentary - Brown
        18 -> Color(0xFF5C6BC0) // Drama - Indigo
        10751 -> Color(0xFF26A69A) // Family - Teal
        14 -> Color(0xFF9C27B0) // Fantasy - Deep Purple
        36 -> Color(0xFF795548) // History - Brown
        27 -> Color(0xFF212121) // Horror - Black
        10402 -> Color(0xFFEC407A) // Music - Pink
        9648 -> Color(0xFF7E57C2) // Mystery - Deep Purple
        10749 -> Color(0xFFF06292) // Romance - Light Pink
        878 -> Color(0xFF00ACC1) // Science Fiction - Cyan
        53 -> Color(0xFF616161) // Thriller - Gray
        10752 -> Color(0xFF6D4C41) // War - Dark Brown
        37 -> Color(0xFFD4A574) // Western - Tan
        10759 -> Color(0xFFEF5350) // Action & Adventure - Light Red
        10762 -> Color(0xFF42A5F5) // Kids - Light Blue
        10763 -> Color(0xFF78909C) // News - Blue Gray
        10764 -> Color(0xFFFF7043) // Reality - Deep Orange
        10765 -> Color(0xFF4A148C) // Sci-Fi & Fantasy - Dark Purple
        10766 -> Color(0xFFBA68C8) // Soap - Light Purple
        10767 -> Color(0xFF66BB6A) // Talk - Green
        10768 -> Color(0xFF8E24AA) // War & Politics - Purple
        else -> Color(0xFF607D8B) // Default - Blue Gray
    }
}