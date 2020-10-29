package alpha.soft.weather.model

data class SoilResponse(
    val data: List<SoilData>
)

data class SoilData(
    val content: String,
    val title: String
)