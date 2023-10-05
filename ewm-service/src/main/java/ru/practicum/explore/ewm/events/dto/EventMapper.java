package ru.practicum.explore.ewm.events.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore.ewm.categories.Category;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;
import ru.practicum.explore.ewm.events.dto.request.EventDtoRequestCreate;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;
import ru.practicum.explore.ewm.users.User;
import ru.practicum.explore.ewm.users.dto.UserMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event fromDtoRequestToNewEvent(EventDtoRequestCreate dto, User initiator, Category category) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .category(category)
                .eventDate(dto.getEventDate())
                .lon(dto.getLocationDto().getLon())
                .lat(dto.getLocationDto().getLat())
                .paid((dto.getPaid() == null) ? false : dto.getPaid())
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration())
                .confirmedRequests(0)
                .initiator(initiator)
                .createdOn(LocalDateTime.now())
                .state(State.PENDING)
                .build();
    }

    public EventDtoShort fromEventToDtoShort(Event event) {
        EventDtoShort dto = new EventDtoShort();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategoryDto(categoryMapper.fromCategoryToDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(userMapper.fromUserToDtoShort(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setViews(event.getViews() == null ? 0 : event.getViews());
        return dto;
    }

    public EventDto fromEventToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(categoryMapper.fromCategoryToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .location(new LocationDto(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .initiator(userMapper.fromUserToDtoShort(event.getInitiator()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests())
                .views(event.getViews() == null ? 0 : event.getViews())
                .build();
    }

    public EventDtoWithAdminComment fromEventToDtoWithAdminComment(Event event) {
        return EventDtoWithAdminComment.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(categoryMapper.fromCategoryToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .location(new LocationDto(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .initiator(userMapper.fromUserToDtoShort(event.getInitiator()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests())
                .views(event.getViews() == null ? 0 : event.getViews())
                .adminComment(event.getAdminComment())
                .build();
    }
}
