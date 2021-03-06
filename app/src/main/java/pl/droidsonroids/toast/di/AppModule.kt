package pl.droidsonroids.toast.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.databinding.ObservableField
import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.toast.BuildConfig
import pl.droidsonroids.toast.app.utils.managers.AnalyticsEventTracker
import pl.droidsonroids.toast.app.utils.managers.AnalyticsEventTrackerImpl
import pl.droidsonroids.toast.repositories.contact.ContactRepository
import pl.droidsonroids.toast.repositories.contact.ContactRepositoryImpl
import pl.droidsonroids.toast.repositories.event.EventsRepository
import pl.droidsonroids.toast.repositories.event.EventsRepositoryImpl
import pl.droidsonroids.toast.repositories.speaker.SpeakersRepository
import pl.droidsonroids.toast.repositories.speaker.SpeakersRepositoryImpl
import pl.droidsonroids.toast.services.*
import pl.droidsonroids.toast.utils.baseUrl
import pl.droidsonroids.toast.viewmodels.DelayViewModel
import pl.droidsonroids.toast.viewmodels.LoadingDelayViewModel
import pl.droidsonroids.toast.viewmodels.facebook.AttendViewModel
import pl.droidsonroids.toast.viewmodels.facebook.WebAttendViewModel
import pl.droidsonroids.toast.viewmodels.speaker.Clock
import pl.droidsonroids.toast.viewmodels.speaker.SystemClockWrapper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private const val ACCEPT = "Accept"
private const val APPLICATION_JSON = "application/json"

@Module(includes = [ViewModelModule::class, FacebookModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideSharedPreference(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideEventsRepository(eventService: EventService): EventsRepository = EventsRepositoryImpl(eventService)

    @Provides
    fun provideSpeakersRepository(speakerService: SpeakerService): SpeakersRepository = SpeakersRepositoryImpl(speakerService)

    @Provides
    fun provideContactRepository(contactService: ContactService, localContactStorage: LocalContactStorage): ContactRepository = ContactRepositoryImpl(contactService, localContactStorage)

    @Singleton
    @Provides
    fun provideContactStorage(sharedPreferences: SharedPreferences): ContactStorage = LocalContactStorage(sharedPreferences)

    //    Replace with FacebookAttendViewModel to use Graph API & attend status check
    @Provides
    fun provideAttendViewModel(): AttendViewModel = WebAttendViewModel()

    @Provides
    fun provideDelayViewModel(clock: Clock): DelayViewModel = LoadingDelayViewModel(clock)

    @Singleton
    @Provides
    fun provideClock(systemClockWrapper: SystemClockWrapper): Clock = systemClockWrapper

    @Singleton
    @Provides
    fun provideRotation() = ObservableField(0f)

    @Provides
    fun provideEventService(httpClient: OkHttpClient): EventService =
            getRetrofitBuilder(httpClient)
                    .create(EventService::class.java)

    @Provides
    fun provideSpeakersService(httpClient: OkHttpClient): SpeakerService =
            getRetrofitBuilder(httpClient)
                    .create(SpeakerService::class.java)

    @Provides
    fun provideContactService(httpClient: OkHttpClient): ContactService =
            getRetrofitBuilder(httpClient)
                    .create(ContactService::class.java)

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsEventTracker(firebaseAnalytics: FirebaseAnalytics): AnalyticsEventTracker = AnalyticsEventTrackerImpl(firebaseAnalytics)

    private fun getRetrofitBuilder(httpClient: OkHttpClient) =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(httpClient)
                    .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .addHttpHeaders()
                    .addHttpLoggingInterceptorIfDebugBuildConfig()
                    .build()

    private fun OkHttpClient.Builder.addHttpHeaders() =
            addInterceptor {
                it.proceed(it.request().newBuilder().header(ACCEPT, APPLICATION_JSON).build())
            }

    private fun OkHttpClient.Builder.addHttpLoggingInterceptorIfDebugBuildConfig(): OkHttpClient.Builder {
        if (BuildConfig.DEBUG) {
            addInterceptor(getHttpLoggingInterceptor())
        }
        return this
    }

    private fun getHttpLoggingInterceptor() =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
}