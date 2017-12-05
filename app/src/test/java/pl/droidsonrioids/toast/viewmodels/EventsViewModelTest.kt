package pl.droidsonrioids.toast.viewmodels

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.internal.operators.maybe.MaybeJust
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import pl.droidsonrioids.toast.data.mapper.toDto
import pl.droidsonrioids.toast.managers.EventsRepository
import pl.droidsonrioids.toast.testEventDetails
import pl.droidsonrioids.toast.testPreviousEvents
import pl.droidsonrioids.toast.testSplitEvents

@RunWith(MockitoJUnitRunner::class)
class EventsViewModelTest {
    @Mock
    lateinit var eventsRepository: EventsRepository
    lateinit var eventsViewModel: EventsViewModel

    @Before
    fun setUp() {
        whenever(eventsRepository.getEvents()).thenReturn(MaybeJust.just(testSplitEvents))
        eventsViewModel = EventsViewModel(eventsRepository)
    }

    @Test
    fun shouldReturnFeaturedEvent() {
        val upcomingEventViewModel = eventsViewModel.featuredEvent.get()

        assertNotNull(upcomingEventViewModel)
        assertThat(upcomingEventViewModel.id, equalTo(testEventDetails.id))
        assertThat(upcomingEventViewModel.title, equalTo(testEventDetails.title))
    }

    @Test
    fun shouldReturnSingletonPreviousEventsList() {
        val previousEvents = eventsViewModel.lastEvents

        assertThat(previousEvents.size, equalTo(1))
        val previousEventDto = previousEvents.first()
        val testPreviousApiEvent = testPreviousEvents.first()
        assertThat(previousEventDto, equalTo(testPreviousApiEvent.toDto()))
    }

}