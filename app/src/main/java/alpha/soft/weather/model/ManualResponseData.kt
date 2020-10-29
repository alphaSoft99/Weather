package alpha.soft.weather.model

data class ManualResponseData(
    val data: ManualData
)

data class ManualData(
    val content: String,
    val iza: List<Iza>,
    val iza_content: String,
    val title: String
)

data class Iza(
    val color: String,
    val name: String
)