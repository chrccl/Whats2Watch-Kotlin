package it.namenotfoundexception.whats2watch.api

import com.google.gson.annotations.SerializedName
import it.namenotfoundexception.whats2watch.model.entities.Movie

data class OMDBSearchResponse(
    @SerializedName("Search") val results: List<Movie> = emptyList(),
    @SerializedName("totalResults") val totalResults: String? = null,
    @SerializedName("Response") val response: String
)
