package alpha.soft.weather.di.module

import alpha.soft.weather.di.scope.AppScope
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class UserApiModule(private val context: Context) {

    @Provides
    @AppScope
    fun provideUserApiService(retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }


    @Provides
    @AppScope
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    @AppScope
    fun provideRetrofitBuilder(preferencesUtil: PreferencesUtil): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder().addInterceptor(interceptor).build()
        GsonBuilder()
            .create()
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(ChuckInterceptor(context))
            .build()
    }

    @AppScope
    @Provides
    fun providesPreferences(): PreferencesUtil {
        val sharedPreferences = context.getSharedPreferences("OauthPrefs", Context.MODE_PRIVATE)
        return PreferencesUtil(context, sharedPreferences)
    }

    @Provides
    @AppScope
    fun providesGson(): Gson {
        return GsonBuilder().create()
    }
}
