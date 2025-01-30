package com.basudev.weatherapp.data.model

data class LocationSearchResult(
    val name: String,      // City name
    val country: String,   // Country code
    val state: String?,    // State (optional)
    val lat: Double,       // Latitude
    val lon: Double        // Longitude
) {
    // Helper property for display
    val displayName: String
        get() = when {
            !state.isNullOrEmpty() -> "$name, $state, $country"
            else -> "$name, $country"
        }
}