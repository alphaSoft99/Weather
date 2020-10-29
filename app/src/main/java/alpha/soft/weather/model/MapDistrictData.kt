package alpha.soft.weather.model

class MapDistrictData : ArrayList<MapDistrictDataItem>()

data class MapDistrictDataItem(
    val boundingbox: List<String>,
    val `class`: String,
    val display_name: String,
    val geojson: Geojson,
    val icon: String,
    val center_map: CenterMap,
    val importance: Double,
    val lat: String,
    val licence: String,
    val zoom: Double,
    val lon: String,
    val osm_id: Int,
    val osm_type: String,
    val place_id: String,
    val type: String
)

data class Geojson(
    val coordinates: List<Any>,
    val type: String
)

data class CenterMap(
    val lat: String,
    val lon: String
)