package pl.droidsonroids.toast

import pl.droidsonroids.toast.data.Page
import pl.droidsonroids.toast.data.api.ApiImage
import pl.droidsonroids.toast.data.api.event.ApiCoordinates
import pl.droidsonroids.toast.data.api.event.ApiEvent
import pl.droidsonroids.toast.data.api.event.ApiEventDetails
import pl.droidsonroids.toast.data.api.event.ApiEventTalk
import pl.droidsonroids.toast.data.api.speaker.ApiSpeaker
import pl.droidsonroids.toast.data.api.speaker.ApiSpeakerDetails
import pl.droidsonroids.toast.data.api.speaker.ApiSpeakerTalk
import pl.droidsonroids.toast.data.dto.ImageDto
import pl.droidsonroids.toast.data.dto.event.EventDto
import pl.droidsonroids.toast.data.dto.event.SplitEvents
import pl.droidsonroids.toast.data.dto.speaker.SpeakerDetailsDto
import pl.droidsonroids.toast.data.dto.speaker.SpeakerTalkDto
import pl.droidsonroids.toast.data.mapper.toDto
import pl.droidsonroids.toast.utils.Constants
import pl.droidsonroids.toast.viewmodels.event.UpcomingEventViewModel
import java.text.SimpleDateFormat
import java.util.*

val testDate: Date = SimpleDateFormat(Constants.Date.PATTERN).parse("1.12.2017")

val testApiImage = ApiImage("http://image.url", "http://thumb.url")

val testApiEvent = ApiEvent(
        id = 0,
        title = "titleFirst",
        date = testDate,
        coverImages = listOf(testApiImage)
)
val testPreviousEvents = listOf(testApiEvent)

val testPreviousEventsPage = Page(items = testPreviousEvents.map { it.toDto() }, pageNumber = 1, allPagesCount = 1)

val testSpeaker = ApiSpeaker(
        id = 0,
        name = "name",
        job = "job",
        avatar = testApiImage
)
val anotherTestSpeaker = ApiSpeaker(
        id = 1,
        name = "name",
        job = "job",
        avatar = testApiImage
)

val testApiEventTalk = ApiEventTalk(
        id = 0,
        title = "title",
        description = "description",
        speaker = testSpeaker
)

val testEventDetails = ApiEventDetails(
        id = 1,
        title = "title",
        date = testDate,
        facebookId = "facebookId",
        placeName = "placeName",
        placeStreet = "placeStreet",
        placeCoordinates = ApiCoordinates(51.1098206, 17.0251941),
        coverImages = listOf(testApiImage),
        photos = listOf(testApiImage),
        eventTalks = listOf(testApiEventTalk)
)

val testSplitEvents = SplitEvents(upcomingEvent = testEventDetails.toDto(), previousEvents = testPreviousEventsPage)

val testSpeakers = listOf(testSpeaker)

val anotherTestSpeakers = listOf(anotherTestSpeaker)

val testSpeakersPage = Page(items = testSpeakers.map { it.toDto() }, pageNumber = 1, allPagesCount = 1)

val anotherTestSpeakersPage = Page(items = anotherTestSpeakers.map { it.toDto() }, pageNumber = 1, allPagesCount = 1)


val testImageDto = ImageDto(
        "http://image.url",
        "http://thumb.url"
)

val testEventDto = EventDto(
        id = 0,
        title = "titleFirst",
        date = testDate,
        coverImages = listOf(testImageDto))

val testSpeakerTalkDto = SpeakerTalkDto(
        id = 1,
        title = "title",
        description = "description",
        event = testEventDto
)

val testApiSpeakerTalk = ApiSpeakerTalk(
        id = 1,
        title = "title",
        description = "description",
        event = testApiEvent
)

val testSpeakerDetailsDto = SpeakerDetailsDto(
        id = 0,
        name = "name",
        job = "job",
        bio = "bio",
        avatar = testImageDto,
        github = "github",
        email = "email",
        website = "website",
        twitter = "twitter",
        talks = listOf(testSpeakerTalkDto)
)

val testApiSpeakerDetails = ApiSpeakerDetails(
        id = 0,
        name = "name",
        job = "job",
        bio = "bio",
        avatar = testApiImage,
        github = "github",
        email = "email",
        website = "website",
        twitter = "twitter",
        talks = listOf(testApiSpeakerTalk)
)

val upcomingEventViewModelWithPhotos = UpcomingEventViewModel(
        id = testEventDetails.id,
        title = testEventDetails.title,
        date = testEventDetails.date,
        placeName = testEventDetails.placeName,
        placeStreet = testEventDetails.placeStreet,
        coverImage = testImageDto,
        photos = listOf(testImageDto),
        coordinates = testEventDetails.placeCoordinates.toDto(),
        locationClickCallback = { _, _ -> },
        seePhotosCallback = { _, _ -> },
        eventClickCallback = { _, _ -> },
        attendCallback = {}
)

val upcomingEventViewModelWithoutPhotos = UpcomingEventViewModel(
        id = testEventDetails.id,
        title = testEventDetails.title,
        date = testEventDetails.date,
        placeName = testEventDetails.placeName,
        placeStreet = testEventDetails.placeStreet,
        coverImage = testImageDto,
        photos = emptyList(),
        coordinates = testEventDetails.placeCoordinates.toDto(),
        locationClickCallback = { _, _ -> },
        seePhotosCallback = { _, _ -> },
        eventClickCallback = { _, _ -> },
        attendCallback = {}
)