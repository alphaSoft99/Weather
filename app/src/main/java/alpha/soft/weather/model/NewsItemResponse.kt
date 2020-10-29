package alpha.soft.weather.model

data class NewsItemResponse(
    val data: NewsItemData,
    val gallery: List<Gallery>
)

data class NewsItemData(
    val content: String,
    val date: String,
    val id: String,
    val title: String,
    val views: String
)

data class Gallery(
    val img: String
)