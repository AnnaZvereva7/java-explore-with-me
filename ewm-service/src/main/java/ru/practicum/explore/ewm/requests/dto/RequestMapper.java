package ru.practicum.explore.ewm.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.explore.ewm.requests.model.Request;

@Component
public class RequestMapper {
    public RequestDto fromRequestToDto(Request request) {
        return new RequestDto(request.getId(), request.getEventId(), request.getRequesterId(), request.getCreated(), request.getStatus());
    }
}
