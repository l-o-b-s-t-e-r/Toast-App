package pl.droidsonrioids.toast.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonrioids.toast.BuildConfig
import pl.droidsonrioids.toast.data.api.EventService
import pl.droidsonrioids.toast.data.api.EventsManager
import pl.droidsonrioids.toast.data.api.EventsManagerImpl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val ACCEPT = "Accept"
private const val APPLICATION_JSON = "application/json"

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule {
    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideApiManager(eventService: EventService): EventsManager = EventsManagerImpl(eventService)

    @Singleton
    @Provides
    fun provideApiService(httpClient: OkHttpClient): EventService =
            Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(httpClient)
                    .build()
                    .create(EventService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        addHeaders(builder)
        addHttpLoggingInterceptorIfDebugBuildConfig(builder)
        return builder.build()
    }

    private fun addHeaders(builder: OkHttpClient.Builder) {
        builder.addInterceptor {
            it.proceed(it.request().newBuilder().header(ACCEPT, APPLICATION_JSON).build())
        }
    }

    private fun addHttpLoggingInterceptorIfDebugBuildConfig(builder: OkHttpClient.Builder) {
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(getHttpLoggingInterceptor())
        }
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}