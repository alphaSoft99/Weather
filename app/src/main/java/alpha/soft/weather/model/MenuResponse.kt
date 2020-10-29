package alpha.soft.weather.model

class MenuResponse : ArrayList<Menu>()

data class Menu(
    val id: String,
    val submenu: List<Submenu>,
    val title: String
)

data class Submenu(
    val category_id: String,
    val content: String,
    val id: String,
    val short_content: String,
    val title: String
)