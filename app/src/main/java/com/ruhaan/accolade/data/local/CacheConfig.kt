package com.ruhaan.accolade.data.local

object CacheConfig {
    val TTL_TRENDING          = hours(1)
    val TTL_EDITORS_PICKS     = hours(24)
    val TTL_PREVIOUS          = hours(3)   // aired in last 30 days
    val TTL_THIS_WEEK         = hours(6)
    val TTL_UPCOMING          = hours(6)
    val TTL_GENRE             = hours(3)
    val TTL_DETAIL            = hours(24)
    val TTL_CREDITS           = hours(24)
    val TTL_REVIEWS           = hours(6)
    val TTL_PERSON            = days(7)
    val TTL_FILMOGRAPHY       = days(7)

    private fun hours(h: Long) = h * 60 * 60 * 1_000L
    private fun days(d: Long)  = d * 24 * 60 * 60 * 1_000L
}