package pl.droidsonroids.toast.repositories

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import pl.droidsonroids.toast.*
import pl.droidsonroids.toast.data.api.speaker.SpeakerDetailsResponse
import pl.droidsonroids.toast.data.api.speaker.SpeakersResponse
import pl.droidsonroids.toast.data.mapper.toDto
import pl.droidsonroids.toast.repositories.speaker.SpeakersRepositoryImpl
import pl.droidsonroids.toast.services.SpeakerService
import pl.droidsonroids.toast.utils.SortingType

class SpeakersRepositoryImplTest : RxTestBase() {
    private val speakerId = 0L

    @Mock
    lateinit var speakerService: SpeakerService

    @InjectMocks
    lateinit var speakerRepository: SpeakersRepositoryImpl


    @Test
    fun shouldReturnSpeakersPage() {
        val allPagesCount = 1
        val pageNumber = 1
        val speakersResponse = SpeakersResponse(testSpeakers, allPagesCount)
        whenever(speakerService.getSpeakers(any(), any(), eq(pageNumber))).thenReturn(Single.just(speakersResponse))
        speakerRepository.getSpeakersPage(pageNumber, SortingType.ALPHABETICAL.toQuery())
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it == testSpeakersPage }
    }

    @Test
    fun shouldReturnSpeakerDetails() {
        val speakersResponse = SpeakerDetailsResponse(testApiSpeakerDetails)
        whenever(speakerService.getSpeaker(speakerId)).thenReturn(Single.just(speakersResponse))
        speakerRepository.getSpeaker(speakerId)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it.id == testSpeakerDetailsDto.id }
                .assertValue { it.bio == testSpeakerDetailsDto.bio }
                .assertValue { it.name == testSpeakerDetailsDto.name }
                .assertValue { it.job == testSpeakerDetailsDto.job }
                .assertValue { it.github == testSpeakerDetailsDto.github }
                .assertValue { it.website == testSpeakerDetailsDto.website }
                .assertValue { it.twitter == testSpeakerDetailsDto.twitter }
                .assertValue { it.email == testSpeakerDetailsDto.email }
                .assertValue { it.talks == listOf(testApiSpeakerTalk.toDto()) }
    }
}

