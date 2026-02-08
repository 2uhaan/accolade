package com.ruhaan.accolade

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ruhaan.accolade.domain.repository.MovieRepository
import com.ruhaan.accolade.navigation.NavGraph
import com.ruhaan.accolade.presentation.schedule.ScheduleViewModel
import com.ruhaan.accolade.ui.theme.AccoladeTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject lateinit var repository: MovieRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      AccoladeTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavGraph()
        }
      }
    }

    // TEMP - Test ScheduleViewModel
    lifecycleScope.launch {
      delay(2000) // Wait for initial load

      // Access ViewModel properly through Hilt
      val scheduleViewModel =
          ViewModelProvider(this@MainActivity, defaultViewModelProviderFactory)[
              ScheduleViewModel::class.java]

      scheduleViewModel.uiState.collect { state ->
        if (!state.isLoading && state.error == null) {
          Log.d("SCHEDULE_TEST", "Grouped sections: ${state.upcomingMovies.size}")
          state.upcomingMovies.forEach {
            Log.d("SCHEDULE_TEST", "Date: ${it.dateFormatted}, Movies: ${it.movies.size}")
          }
        }
      }
    }
  }
}
