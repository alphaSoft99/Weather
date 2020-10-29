package alpha.soft.weather.model

data class PollutantsResponse(
    val data: PollutantsData
)

data class PollutantsData(
    val main: List<MainData>,
    val specific: List<MainData>
)

data class MainData(
    val title: String,
    val value_1: String,
    val value_2: String,
    val value_3: String
)

