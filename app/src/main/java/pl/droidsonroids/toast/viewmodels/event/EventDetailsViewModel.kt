package pl.droidsonroids.toast.viewmodels.event

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import pl.droidsonroids.toast.app.utils.managers.AnalyticsEventTracker
import pl.droidsonroids.toast.data.dto.ImageDto
import pl.droidsonroids.toast.data.dto.event.CoordinatesDto
import pl.droidsonroids.toast.data.dto.event.EventDetailsDto
import pl.droidsonroids.toast.data.dto.event.EventTalkDto
import pl.droidsonroids.toast.data.mapper.toDto
import pl.droidsonroids.toast.data.mapper.toViewModel
import pl.droidsonroids.toast.repositories.event.EventsRepository
import pl.droidsonroids.toast.utils.*
import pl.droidsonroids.toast.viewmodels.DelayViewModel
import pl.droidsonroids.toast.viewmodels.LoadingViewModel
import pl.droidsonroids.toast.viewmodels.NavigatingViewModel
import pl.droidsonroids.toast.viewmodels.facebook.AttendViewModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val DEFAULT_GRADIENT_COLOR = 0xA0000000.toInt()
private const val GRADIENT_COLOR_MASK = 0xE0FFFFFF.toInt()
private const val WINDOW_MAX_ROTATION_DEGREES = 5f
private const val WINDOW_DEFAULT_ROTATION_DEGREES = 0f

class EventDetailsViewModel @Inject constructor(
        private val eventsRepository: EventsRepository,
        attendViewModel: AttendViewModel,
        private val analyticsEventTracker: AnalyticsEventTracker,
        delayViewModel: DelayViewModel,
        val rotation: ObservableField<Float>
) : ViewModel(), LoadingViewModel, DelayViewModel by delayViewModel, NavigatingViewModel, AttendViewModel by attendViewModel {
    override val navigationSubject: PublishSubject<NavigationRequest> = navigationRequests
    override val loadingStatus: ObservableField<LoadingStatus> = ObservableField(LoadingStatus.PENDING)
    val eventId = ObservableField(Constants.NO_ID)
    override val isFadingEnabled get() = true
    val title = ObservableField("")
    val date = ObservableField<Date>()
    val placeName = ObservableField("")
    val placeStreet = ObservableField("")
    val coverImage = ObservableField<ImageDto?>()
    val photosAvailable = ObservableField(false)
    val gradientColor = ObservableField(DEFAULT_GRADIENT_COLOR)
    val loadFromCache = ObservableField(true)

    val coverImageLoadingFinishedSubject: PublishSubject<Unit> = PublishSubject.create()

    val onLoadingFinished: () -> Unit = {
        coverImageLoadingFinishedSubject.onNext(Unit)
    }

    val onGradientColorLoaded: (Int) -> Unit = {
        gradientColor.set(it and GRADIENT_COLOR_MASK)
    }
    private var coordinates: CoordinatesDto? = null
    val isSpeakersLabelVisible = ObservableField(false)

    val eventSpeakersSubject: BehaviorSubject<List<EventSpeakerItemViewModel>> = BehaviorSubject.create()

    var photos: List<ImageDto> = emptyList()

    private var eventsDisposable = Disposables.disposed()

    fun onPhotosClick() {
        navigationSubject.onNext(NavigationRequest.Photos(photos))
        analyticsEventTracker.logEventDetailsSeePhotosEvent(eventId.get())
    }

    fun onLocationClick() {
        coordinates?.let {
            navigationSubject.onNext(NavigationRequest.Map(it, placeName.get()))
            analyticsEventTracker.logEventDetailsTapMeetupPlaceEvent()
        }
    }

    fun onTitleLongClick() = consume {
        if (date.get().isToday) {
            with(rotation) {
                when (get()) {
                    WINDOW_DEFAULT_ROTATION_DEGREES -> set(WINDOW_MAX_ROTATION_DEGREES)
                    WINDOW_MAX_ROTATION_DEGREES -> set(WINDOW_DEFAULT_ROTATION_DEGREES)
                }
            }
        }
    }

    fun init(id: Long, coverImage: ImageDto?) {
        if (eventId.get() == Constants.NO_ID) {
            eventId.set(id)
            this.coverImage.set(coverImage)
        }
    }

    private fun loadEvent() {
        loadingStatus.set(LoadingStatus.PENDING)
        updateLastLoadingStartTime()
        eventsDisposable = eventsRepository.getEvent(eventId.get())
                .let(::addLoadingDelay)
                .subscribeBy(
                        onSuccess = ::onEventLoaded,
                        onError = ::onEventLoadError
                )
    }

    private fun onEventLoaded(eventDetailsDto: EventDetailsDto) {
        loadingStatus.set(LoadingStatus.SUCCESS)
        eventDetailsDto.let {
            title.set(it.title)
            date.set(it.date)
            placeName.set(it.placeName)
            placeStreet.set(it.placeStreet)
            coverImage.run { set(get() ?: it.coverImages.firstOrNull()) }
            photosAvailable.set(it.photos.isNotEmpty())
            photos = it.photos
            coordinates = it.coordinates
            onTalksLoaded(it.talks)
            setEvent(it.facebookId, it.date, SourceAttending.EVENT_DETAILS)
        }
    }

    private fun onTalksLoaded(talks: List<EventTalkDto>) {
        val eventSpeakerViewModels = talks.map { it.toViewModel(::onReadMore, ::onSpeakerClick) }
        eventSpeakersSubject.onNext(eventSpeakerViewModels)
        isSpeakersLabelVisible.set(eventSpeakerViewModels.isNotEmpty())
    }

    private fun onReadMore(eventSpeakerItemViewModel: EventSpeakerItemViewModel) {
        val eventTalkDto = eventSpeakerItemViewModel.toDto()
        navigationSubject.onNext(NavigationRequest.EventTalkDetails(eventTalkDto))
        Timber.d("onReadMore: ${eventSpeakerItemViewModel.id}")
        analyticsEventTracker.logEventDetailsReadMoreEvent(eventTalkDto.title)
    }

    private fun onSpeakerClick(speakerTalkId: Long?, speakerId: Long, speakerName: String, avatar: ImageDto?) {
        navigationSubject.onNext(NavigationRequest.SpeakerDetails(speakerId, avatar, speakerTalkId))
        analyticsEventTracker.logEventDetailsShowSpeakerEvent(speakerName)
    }

    private fun onEventLoadError(throwable: Throwable) {
        loadingStatus.set(LoadingStatus.ERROR)
        Timber.e(throwable, "Something went wrong when fetching event details with id = ${eventId.get()}")
    }

    override fun retryLoading() {
        loadEvent()
    }

    override fun onCleared() {
        dispose()
        eventsDisposable.dispose()
    }

    fun onTransitionEnd() {
        if (loadFromCache.get()) {
            loadFromCache.set(false)
            loadEvent()
        }
    }

    fun invalidateLoading() {
        //        Due to shared element transition bug https://github.com/UweTrottmann/SeriesGuide/issues/522
        loadingStatus.notifyChange()
    }
}