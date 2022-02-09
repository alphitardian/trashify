package com.alphitardian.trashappta.di

import android.content.Context
import com.alphitardian.trashappta.BuildConfig
import com.alphitardian.trashappta.data.datastore.AppDatastore
import com.alphitardian.trashappta.data.image.remote.network.ImageApi
import com.alphitardian.trashappta.data.quiz.remote.network.QuizApi
import com.alphitardian.trashappta.data.user.remote.network.UserApi
import com.alphitardian.trashappta.data.user.remote.network.UserInterceptor
import com.alphitardian.trashappta.data.waste.remote.network.WasteApi
import com.alphitardian.trashappta.utils.IMAGE_UPLOAD_URL
import com.alphitardian.trashappta.utils.PRODUCTION_URL
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainWasteRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WasteHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @WasteHttpClient
    @Provides
    @Singleton
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        datastore: AppDatastore,
        @MainWasteRetrofit retrofit: Retrofit.Builder
    ): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        return OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(
                UserInterceptor(
                    datastore = datastore,
                    loggingInterceptor = loggingInterceptor,
                    retrofit = retrofit
                )
            )
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @ImageHttpClient
    @Provides
    @Singleton
    fun provideImageHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @ExperimentalSerializationApi
    @MainWasteRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(PRODUCTION_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
    }

    @ImageRetrofit
    @Provides
    @Singleton
    fun provideImageRetrofit(): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(IMAGE_UPLOAD_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
    }

    @Provides
    @Singleton
    fun provideUserService(
        @MainWasteRetrofit retrofit: Retrofit.Builder,
        @WasteHttpClient client: OkHttpClient,
    ): UserApi = retrofit.client(client).build().create()

    @Provides
    @Singleton
    fun provideWasteService(
        @MainWasteRetrofit retrofit: Retrofit.Builder,
        @WasteHttpClient client: OkHttpClient,
    ): WasteApi = retrofit.client(client).build().create()

    @Provides
    @Singleton
    fun provideQuizService(
        @MainWasteRetrofit retrofit: Retrofit.Builder,
        @WasteHttpClient client: OkHttpClient,
    ): QuizApi = retrofit.client(client).build().create()

    @Provides
    @Singleton
    fun provideImageService(
        @ImageRetrofit retrofit: Retrofit.Builder,
        @ImageHttpClient client: OkHttpClient,
    ): ImageApi = retrofit.client(client).build().create()

    @Provides
    @Singleton
    fun providePlaceClient(@ApplicationContext context: Context): PlacesClient =
        Places.createClient(context)

    @Provides
    @Singleton
    fun provideGeocodingContext(): GeoApiContext =
        GeoApiContext.Builder().apiKey(BuildConfig.MAP_KEY).build()
}