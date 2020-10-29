package alpha.soft.weather.model

data class FilterResponse(
    val month: List<Month>,
    val year: List<String>
)

data class Month(
    val name: String
)