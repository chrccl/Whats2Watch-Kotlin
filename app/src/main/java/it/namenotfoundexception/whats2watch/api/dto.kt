package it.namenotfoundexception.whats2watch.api

// per search e discover
data class TmdbSearchResponse(
    val page: Int,
    val results: List<TmdbMovieResultDto>,
    val total_pages: Int,
    val total_results: Int
)

data class TmdbMovieResultDto(
    val id: Int,
    val title: String,
    val release_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Float?
)

// per dettaglio
data class TmdbMovieDetailDto(
    val id: Int,
    val title: String,
    val release_date: String?,
    val runtime: Int?,
    val genres: List<GenreDto>,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Float?
)

data class TmdbGenreListResponse(val genres: List<GenreDto>)

data class GenreDto(
    val id: Int,
    val name: String
)

// per credits
data class TmdbCreditsResponse(
    val id: Int,
    val cast: List<CastDto>,
    val crew: List<CrewDto>
)

data class CastDto(
    val name: String,
    val order: Int
)

data class CrewDto(
    val name: String,
    val job: String
)

// per ricerca persona
data class TmdbPersonSearchResponse(
    val page: Int,
    val results: List<TmdbPersonDto>,
    val total_results: Int,
    val total_pages: Int
)

data class TmdbPersonDto(
    val id: Int,
    val name: String
)

data class TmdbReviewsResponse(
    val page: Int,
    val results: List<TmdbReviewDto>,
    val total_pages: Int,
    val total_results: Int
)

data class TmdbReviewDto(
    val author: String,
    val content: String,
    val created_at: String,
    val author_details: TmdbAuthorDetails?
)

data class TmdbAuthorDetails(
    val rating: Float?
)