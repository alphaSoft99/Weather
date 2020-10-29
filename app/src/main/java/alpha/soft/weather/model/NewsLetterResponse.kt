package alpha.soft.weather.model

data class NewsLetterResponse(
    val data: List<NewsLetterData>
)

data class NewsLetterData(
    val category_title: String,
    val color: String,
    val izv: String,
    val number: Int,
    val title: String,
    val value: String
)