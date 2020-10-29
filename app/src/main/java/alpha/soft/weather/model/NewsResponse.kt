package alpha.soft.weather.model

data class NewsResponse(
    val count: String,
    val data: List<NewsData>
)

data class NewsData(
    val content: String,
    val date: String,
    val id: String,
    val title: String,
    val url: String,
    val views: String
)