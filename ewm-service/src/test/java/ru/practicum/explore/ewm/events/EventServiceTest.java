package ru.practicum.explore.ewm.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.explore.ewm.categories.Category;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.common.CommonConstant;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.dto.RequestDtoCount;
import ru.practicum.explore.ewm.events.dto.EventDtoRequest;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;
import ru.practicum.explore.ewm.events.model.StateAction;
import ru.practicum.explore.ewm.requests.RequestRepository;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;
import ru.practicum.explore.stats.client.StatsClient;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatsMapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository repository;
    @Mock
    private StatsClient client;
    @Mock
    private CategoryService categoryService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private StatsMapper statsMapper;


    private EventService service;

    @BeforeEach
    void setup() {
        service = new EventService(client, repository, categoryService, requestRepository, requestMapper, statsMapper);
    }


    @Test
    void findByUserId_whenOk() throws UnsupportedEncodingException {
        Event event = Event.builder().id(2L).title("title").build();
        List<Event> events = List.of(event);
        when(repository.findByUserId(1L, new OffsetBasedPageRequest(0, 10, Sort.by("id").ascending()))).thenReturn(events);
        when(client.get(LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                LocalDateTime.of(2050, 12, 31, 0, 0, 0), List.of("/events/2"), false))
                .thenReturn(List.of(new StatisticDto("app", "/events/2", 3)));
        when(requestRepository.countRequestsByEventId(List.of(2L))).thenReturn(List.of(new RequestDtoCount(2L, 2)));
        List<Event> eventsActual = service.findByUserId(1L, 0, 10);

        assertEquals(3, eventsActual.get(0).getViews());
        assertEquals(2, eventsActual.get(0).getConfirmedRequests());
    }

    @Test
    void findEventById() {
    }

    @Test
    void updateByAdmin_whenOk() throws UnsupportedEncodingException {
        LocalDateTime eventDate = LocalDateTime.of(2023, 10, 10, 12, 0, 0);
        //todo clock
        EventDtoRequest dto = EventDtoRequest.builder()
                .title("title1New").stateAction(StateAction.PUBLISH_EVENT).build();
        Event event = Event.builder()
                .title("title1").id(1L).state(State.PENDING).requestModeration(true).eventDate(eventDate).build();
        Event expectedEvent = Event.builder().title("title1New").id(1L).state(State.PUBLISHED).requestModeration(true)
                .eventDate(eventDate).views(4).build();
        StatisticDto statisticDto = new StatisticDto("app", "/events/1", 4);
        //RequestDtoCount requestDtoCount = new RequestDtoCount(1L, 1);
        when(repository.findById(1L)).thenReturn(Optional.of(event)); //or "Event with id=" + id + " was not found" NotFoundException
        when(client.get(any(LocalDateTime.class), any(LocalDateTime.class), eq(List.of("/events/1")), eq(false))).thenReturn(List.of(statisticDto));
        when(statsMapper.getEventId(statisticDto)).thenReturn(1L);
        when(requestRepository.countRequestsByEventId(List.of(1L))).thenReturn(List.of());
        when(repository.saveAndFlush(expectedEvent)).thenReturn(expectedEvent);

        Event actualEvent = service.updateByAdmin(dto, 1L);
        assertEquals(State.PUBLISHED, actualEvent.getState());
        assertEquals("title1New", actualEvent.getTitle());

    }

    @Test
    void findWithConditions_Public_WhenTextCategoriesPaid() {
        Event event = Event.builder()
                .description("text")
                .category(new Category(1, "cat1"))
                .state(State.PUBLISHED)
                .id(2L)
                .build();
        LocalDateTime rangeStart = LocalDateTime.parse("2020-09-12 12:00:00", CommonConstant.FORMATTER);
        when(repository.findWithConditions(null, List.of(State.PUBLISHED), "text", List.of(1), false,
                rangeStart, null, new OffsetBasedPageRequest(0, 10, Sort.unsorted())))
                .thenReturn(List.of(event));
        //todo mock for addViewsAndCountRequests
        List<Event> actualEvents = service.findWithConditions("text", List.of(1), false, rangeStart, null, false, null, 0, 10);

        assertEquals(1, actualEvents.size());
        assertEquals("text", actualEvents.get(0).getDescription());
    }
}