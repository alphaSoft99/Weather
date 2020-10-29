package alpha.soft.weather.model

data class AreaResponse(
    val data: List<AreaData>
)

data class AreaData(
    val id: String,
    val lat: String,
    val lon: String,
    val title: String
)

data class AreaWithAllItemData(
    val area : AreaData,
    val allItem : List<AllItem>
)