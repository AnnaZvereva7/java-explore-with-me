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
import ru.practicum.explore.ewm.events.dto.EventDtoWithAdminComment;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.dto.request.AdminCommentDto;
import ru.practicum.explore.ewm.events.dto.request.EventDtoRequestUpdateAdmin;
import ru.practicum.explore.ewm.events.dto.request.EventsDtoConfirmation;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;
import ru.practicum.explore.ewm.events.model.stateAction.StateActionAdmin;
import ru.practicum.explore.ewm.exceptions.AccessException;
import ru.practicum.explore.ewm.requests.RequestRepository;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;
import ru.practicum.explore.stats.client.StatsClient;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatsMapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    @Mock
    private EventMapper eventMapper;


    private EventService service;

    @BeforeEach
    void setup() {
        service = new EventService(client, repository, categoryService, requestRepository, requestMapper, statsMapper, eventMapper);
    }


    @Test
    void findByUserId_whenOk() throws UnsupportedEncodingException {
        Event event = Event.builder().id(2L).title("title").publishedOn(LocalDateTime.now().minusMonths(1)).build();
        List<Event> events = List.of(event);
        StatisticDto statisticDto = new StatisticDto("app", "/events/2", 3);

        when(repository.findByUserId(1L, new OffsetBasedPageRequest(0, 10, Sort.by("id").ascending()))).thenReturn(events);
        when(client.get(any(), any(), eq(List.of("/events/2")), eq(true)))
                .thenReturn(List.of(statisticDto));
        when(statsMapper.getEventId(statisticDto)).thenReturn(2L);
        when(requestRepository.countRequestsByEventId(List.of(2L))).thenReturn(List.of(new RequestDtoCount(2L, 2)));

        List<Event> eventsActual = service.findByUserId(1L, 0, 10);

        assertEquals(3, eventsActual.get(0).getViews());
        assertEquals(2, eventsActual.get(0).getConfirmedRequests());
    }

    @Test
    void findEventById() {
    }

    @Test
    void updateByAdmin_Revision_whenOk() throws UnsupportedEncodingException {
        LocalDateTime now = LocalDateTime.now();

        EventDtoRequestUpdateAdmin dto = EventDtoRequestUpdateAdmin.builder()
                .stateAction(StateActionAdmin.SEND_TO_REVISION).adminComment("some comment").build();
        dto.setTitle("title1New");
        Event event = Event.builder()
                .id(1L).title("title1").state(State.PENDING).requestModeration(true).eventDate(now.plusDays(3)).build();
        Event expectedEvent = Event.builder().title("title1New").id(1L).state(State.REVISION).adminComment("some comment").requestModeration(true)
                .eventDate(now.plusDays(3)).views(4).confirmedRequests(0).build();
        StatisticDto statisticDto = new StatisticDto("app-ewm", "/events/1", 4);

        when(repository.findById(1L)).thenReturn(Optional.of(event)); //or "Event with id=" + id + " was not found" NotFoundException
        when(repository.saveAndFlush(expectedEvent)).thenReturn(expectedEvent);

        Event actualEvent = service.updateByAdmin(dto, 1L);

        assertEquals(State.REVISION, actualEvent.getState());
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

    @Test
    void addAdminComment_whenOk() {
        AdminCommentDto dto = new AdminCommentDto("some new comment");
        Event event1 = Event.builder()
                .id(1L).state(State.PENDING).build();
        Event event2 = Event.builder()
                .id(1L).state(State.PENDING).adminComment("some new comment").build();
        when(repository.findById(1L)).thenReturn(Optional.of(event1));
        when(repository.saveAndFlush(event1)).thenReturn(event2);
        Event actualEvent = service.addAdminComment(dto, 1L);

        assertEquals(1L, actualEvent.getId());
        assertEquals(State.PENDING, actualEvent.getState());
        assertEquals("some new comment", actualEvent.getAdminComment());
    }

    @Test
    void addAdminComment_whenWrongState() {
        AdminCommentDto dto = new AdminCommentDto("some new comment");
        Event event1 = Event.builder()
                .id(1L).state(State.PUBLISHED).build();
        when(repository.findById(1L)).thenReturn(Optional.of(event1));
        Throwable thrown = catchThrowable(() -> {
            service.addAdminComment(dto, 1L);
        });
        assertThat(thrown).isInstanceOf(AccessException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void moderationEvents_Publish_whenOk() {
        LocalDateTime now = LocalDateTime.now();
        EventsDtoConfirmation dto = new EventsDtoConfirmation(List.of(1L, 2L), StateActionAdmin.PUBLISH_EVENT);
        Event event1 = Event.builder().id(1L).state(State.PENDING).eventDate(now.plusDays(2)).build();
        Event event2 = Event.builder().id(2L).state(State.PENDING).eventDate(now.plusDays(3)).build();
        EventDtoWithAdminComment dtoEv1 = EventDtoWithAdminComment.builder().id(1L).state(State.PUBLISHED).eventDate(now.plusDays(2)).build();
        EventDtoWithAdminComment dtoEv2 = EventDtoWithAdminComment.builder().id(2L).state(State.PUBLISHED).eventDate(now.plusDays(3)).build();
        when(repository.findByStateAndIds(List.of(1L, 2L), State.PENDING)).thenReturn(List.of(event1, event2));
        when(eventMapper.fromEventToDtoWithAdminComment(event1)).thenReturn(dtoEv1);
        when(eventMapper.fromEventToDtoWithAdminComment(event2)).thenReturn(dtoEv2);
        when(repository.saveAllAndFlush(List.of(event1, event2))).thenReturn(List.of(event1, event2));
        Map<String, List<EventDtoWithAdminComment>> resultMap = service.moderationEvents(dto);

        assertEquals(2, resultMap.size());
        assertEquals(2, resultMap.get("published").size());
        assertEquals(0, resultMap.get("too_close_eventDate").size());
    }

    @Test
    void moderationEvents_Publish_when1wrongDate() {
        LocalDateTime now = LocalDateTime.now();
        EventsDtoConfirmation dto = new EventsDtoConfirmation(List.of(1L, 2L), StateActionAdmin.PUBLISH_EVENT);
        Event event1 = Event.builder().id(1L).state(State.PENDING).eventDate(now.plusDays(2)).build();
        Event event2 = Event.builder().id(2L).state(State.PENDING).eventDate(now).build();
        EventDtoWithAdminComment dtoEv1 = EventDtoWithAdminComment.builder().id(1L).state(State.PUBLISHED).eventDate(now.plusDays(2)).build();
        EventDtoWithAdminComment dtoEv2 = EventDtoWithAdminComment.builder().id(2L).state(State.PENDING).eventDate(now).build();
        when(repository.findByStateAndIds(List.of(1L, 2L), State.PENDING)).thenReturn(List.of(event1, event2));
        when(eventMapper.fromEventToDtoWithAdminComment(event1)).thenReturn(dtoEv1);
        when(eventMapper.fromEventToDtoWithAdminComment(event2)).thenReturn(dtoEv2);
        when(repository.saveAllAndFlush(List.of(event1))).thenReturn(List.of(event1));
        Map<String, List<EventDtoWithAdminComment>> resultMap = service.moderationEvents(dto);

        assertEquals(2, resultMap.size());
        assertEquals(1, resultMap.get("published").size());
        assertEquals(1, resultMap.get("published").get(0).getId());
        assertEquals(State.PUBLISHED, resultMap.get("published").get(0).getState());
        assertEquals(1, resultMap.get("too_close_eventDate").size());
        assertEquals(2, resultMap.get("too_close_eventDate").get(0).getId());
        assertEquals(State.PENDING, resultMap.get("too_close_eventDate").get(0).getState());
    }

    @Test
    void moderationEvents_Cancel_whenOk() {
        LocalDateTime now = LocalDateTime.now();
        EventsDtoConfirmation dto = new EventsDtoConfirmation(List.of(1L, 2L), StateActionAdmin.REJECT_EVENT);
        Event event1 = Event.builder().id(1L).state(State.PENDING).eventDate(now.plusDays(2)).build();
        Event event2 = Event.builder().id(2L).state(State.PENDING).eventDate(now).build();
        EventDtoWithAdminComment dtoEv1 = EventDtoWithAdminComment.builder().id(1L).state(State.CANCELED).eventDate(now.plusDays(2)).build();
        EventDtoWithAdminComment dtoEv2 = EventDtoWithAdminComment.builder().id(2L).state(State.CANCELED).eventDate(now).build();
        when(repository.findByStateAndIds(List.of(1L, 2L), State.PENDING)).thenReturn(List.of(event1, event2));
        when(eventMapper.fromEventToDtoWithAdminComment(event1)).thenReturn(dtoEv1);
        when(eventMapper.fromEventToDtoWithAdminComment(event2)).thenReturn(dtoEv2);
        when(repository.saveAllAndFlush(List.of(event1, event2))).thenReturn(List.of(event1, event2));
        Map<String, List<EventDtoWithAdminComment>> resultMap = service.moderationEvents(dto);

        assertEquals(1, resultMap.size());
        assertEquals(2, resultMap.get("Canceled").size());
        assertEquals(1, resultMap.get("Canceled").get(0).getId());
        assertEquals(2, resultMap.get("Canceled").get(1).getId());
        assertEquals(State.CANCELED, resultMap.get("Canceled").get(0).getState());
    }

    @Test
    void moderationEvents_Revision_whenWithAndWithOutAdminComment() {
        LocalDateTime now = LocalDateTime.now();
        EventsDtoConfirmation dto = new EventsDtoConfirmation(List.of(1L, 2L), StateActionAdmin.SEND_TO_REVISION);
        Event event1 = Event.builder().id(1L).state(State.PENDING).eventDate(now.plusDays(2)).adminComment("some comment").build();
        Event event2 = Event.builder().id(2L).state(State.PENDING).eventDate(now).build();
        EventDtoWithAdminComment dtoEv1 = EventDtoWithAdminComment.builder().id(1L).state(State.REVISION).adminComment("some comment").eventDate(now.plusDays(2)).build();
        EventDtoWithAdminComment dtoEv2 = EventDtoWithAdminComment.builder().id(2L).state(State.PENDING).eventDate(now).build();
        when(repository.findByStateAndIds(List.of(1L, 2L), State.PENDING)).thenReturn(List.of(event1, event2));
        when(eventMapper.fromEventToDtoWithAdminComment(event1)).thenReturn(dtoEv1);
        when(eventMapper.fromEventToDtoWithAdminComment(event2)).thenReturn(dtoEv2);
        when(repository.saveAllAndFlush(List.of(event1))).thenReturn(List.of(event1));
        Map<String, List<EventDtoWithAdminComment>> resultMap = service.moderationEvents(dto);

        assertEquals(2, resultMap.size());
        assertEquals(1, resultMap.get("Sent_to_revision").size());
        assertEquals(1, resultMap.get("Sent_to_revision").get(0).getId());
        assertEquals(State.REVISION, resultMap.get("Sent_to_revision").get(0).getState());
        assertEquals("some comment", resultMap.get("Sent_to_revision").get(0).getAdminComment());
        assertEquals(1, resultMap.get("Need_comment_by_admin").size());
        assertEquals(2, resultMap.get("Need_comment_by_admin").get(0).getId());
        assertEquals(State.PENDING, resultMap.get("Need_comment_by_admin").get(0).getState());
        assertEquals(null, resultMap.get("Need_comment_by_admin").get(0).getAdminComment());
    }


}