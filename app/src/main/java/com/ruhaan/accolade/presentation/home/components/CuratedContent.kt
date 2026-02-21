package com.ruhaan.accolade.presentation.home.components

import com.ruhaan.accolade.domain.model.MediaType

data class CuratedItem(
    val tmdbId: Int,
    val mediaType: MediaType,
)

object EditorsPicks {
  val items: List<CuratedItem> =
      listOf(
          CuratedItem(tmdbId = 80443, mediaType = MediaType.TV_SHOW), // The Loudest Voice
          CuratedItem(tmdbId = 126301, mediaType = MediaType.TV_SHOW), // Super Pumped
          CuratedItem(tmdbId = 99048, mediaType = MediaType.TV_SHOW), // Start-Up
          CuratedItem(tmdbId = 60863, mediaType = MediaType.TV_SHOW), // Haikyuu!!
          CuratedItem(
              tmdbId = 212333,
              mediaType = MediaType.TV_SHOW,
          ), // Mussolini
          CuratedItem(tmdbId = 438631, mediaType = MediaType.MOVIE), // Dune
          CuratedItem(tmdbId = 670292, mediaType = MediaType.MOVIE), // The Creator
          CuratedItem(
              tmdbId = 373571,
              mediaType = MediaType.MOVIE,
          ), // Godzilla: King of the Monsters
      )
}
