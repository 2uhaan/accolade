package com.ruhaan.accolade.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ruhaan.accolade.presentation.home.components.ModernTopBar
import com.ruhaan.accolade.presentation.search.SearchScreen
import com.ruhaan.accolade.presentation.search.SearchViewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.ExperimentalSharedTransitionApi

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavigationScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // REPLACE the if/else block with this:
    SharedTransitionLayout {
        AnimatedContent(
            targetState = isSearchExpanded,
            label = "searchTransition",
        ) { expanded ->
            if (expanded) {
                SearchScreen(
                    navController = navController,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchClose = {
                        isSearchExpanded = false
                        searchQuery = ""
                        searchViewModel.clearSearch()
                    },
                    viewModel = searchViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ModernTopBar(
                        isSearchExpanded = false,
                        searchQuery = "",
                        onSearchQueryChange = {},
                        onSearchClick = { isSearchExpanded = true },
                        onSearchClose = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    content()
                }
            }
        }
    }
}
