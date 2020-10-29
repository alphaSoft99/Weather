package alpha.soft.weather.model

data class RecommendationResponse(
    val data: List<RecommendationData>
)

data class RecommendationData(
    val content_1: String,
    val color: String,
    val content_2: String,
    val title: String
)