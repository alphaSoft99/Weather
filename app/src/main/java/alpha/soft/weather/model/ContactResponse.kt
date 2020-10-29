package alpha.soft.weather.model

data class ContactResponse(
    val data: ContactData
)

data class ContactData(
    val address: String,
    val email: List<String>,
    val phone: List<String>,
    val title: String
)