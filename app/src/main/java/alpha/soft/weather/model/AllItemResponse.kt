package alpha.soft.weather.model

data class AllItemResponse(
    val data: List<AllItem>
)

data class AllItem(
    val category_title: String,
    val color: String,
    val id: String,
    val region_id: String,
    val lat: String,
    val lon: String,
    val title: String,
    val si: String?
)