package alpha.soft.weather.network

import alpha.soft.weather.model.*
import retrofit2.Response
import retrofit2.http.*

interface PostApi {

    @GET("{lang}/api/mobile/point")
    suspend fun getAllItems(
        @Path("lang") lang: String
    ): Response<AllItemResponse>

    @GET("{lang}/api/mobile/regions")
    suspend fun getAreas(@Path("lang") lang: String): Response<AreaResponse>

    @GET("{lang}/api/mobile/point/view/{id}")
    suspend fun getItem(@Path("lang") lang: String, @Path("id") id: String): Response<ItemViewResponse>

    @GET("{lang}/api/mobile/menu")
    suspend fun getMenu(@Path("lang") lang: String): Response<MenuResponse>

    @GET("{lang}/api/mobile/news")
    suspend fun getNews(@Path("lang") lang: String): Response<NewsResponse>

    @GET("{lang}/api/mobile/criteria")
    suspend fun getCriteria(@Path("lang") lang: String): Response<CriteriaResponse>

    @GET("{lang}/api/mobile/recommendations")
    suspend fun getRecommendation(@Path("lang") lang: String): Response<RecommendationResponse>

    @GET("{lang}/api/mobile/pollutants")
    suspend fun getPollutants(@Path("lang") lang: String): Response<PollutantsResponse>

    @GET("{lang}/api/mobile/newsletter/index")
    suspend fun getNewsLetter(@Path("lang") lang: String, @Query("month") month: String, @Query("year") year: String): Response<NewsLetterResponse>

    @GET("{lang}/api/mobile/newsletter/filter")
    suspend fun getFilter(@Path("lang") lang: String): Response<FilterResponse>

    @GET("{lang}/api/mobile/soil")
    suspend fun getSoil(@Path("lang") lang: String, @Query("year") year: String): Response<SoilResponse>

    @GET("{lang}/api/mobile/soil/filter")
    suspend fun getSoilFilter(@Path("lang") lang: String): Response<SoilFilterResponse>

    @GET("{lang}/api/mobile/contacts")
    suspend fun getContact(@Path("lang") lang: String): Response<ContactResponse>

    @GET("{lang}/api/mobile/manual")
    suspend fun getMapInfo(@Path("lang") lang: String): Response<ManualResponseData>

    @GET("{lang}/api/mobile/news/view/{id}")
    suspend fun getNewsItem(@Path("lang") lang: String, @Path("id") id: String): Response<NewsItemResponse>

    @FormUrlEncoded
    @POST("{lang}/api/mobile/contacts/action")
    suspend fun sendMessage(@Path("lang") lang: String, @Field("name") name: String, @Field("pochta") email: String, @Field("message") info: String): Response<ConnectResponse>
}