package alpha.soft.weather.model

data class ItemViewResponse(
    val concentrations: List<Concentration>,
    val data: ItemData,
    val graphic: List<Graphic>,
    val pogoda: Pogoda,
    val concentrations_date: String,
    val recommendations: Recommendations
)

data class Concentration(
    val color: String,
    val k_value: String,
    val option_value: String,
    val title_value: String
)

data class ItemData(
    val category_title: String,
    val color_si: String,
    val date: String,
    val id: String,
    val lat: String,
    val lon: String,
    val si: String,
    val status_moderator_main: String,
    val title: String
)

data class Graphic(
    val color_si: String,
    val date: String,
    val si: String
)

data class Pogoda(
    val city: String,
    val content: String,
    val img: String,
    val temperature: String,
    val title: String
)

data class Recommendations(
    val color_si: String,
    val content: String,
    val icon: String
)