package alpha.soft.weather.model

data class CriteriaResponse(
    val data: List<CriteriaData>
)

data class CriteriaData(
    val category_title: String,
    val color: String,
    val content: String,
    val id: String,
    val izv: String,
    val title: String
)