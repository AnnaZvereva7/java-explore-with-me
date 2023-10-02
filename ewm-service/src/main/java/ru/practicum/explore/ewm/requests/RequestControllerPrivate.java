package ru.practicum.explore.ewm.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.requests.dto.RequestDto;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestControllerPrivate {
    private final RequestService service;
    private final RequestMapper mapper;

    @GetMapping
    public List<RequestDto> getRequestByUser(@PathVariable Long userId) {
        log.info("Private: get all requests by user Id with {}", userId);
        return service.findByRequesterId(userId)
                .stream()
                .map(mapper::fromRequestToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId,
                                 @RequestParam Long eventId) {
        log.info("Private: add request by user id {} to event id {}", userId, eventId);
        return mapper.fromRequestToDto(service.addrequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Private: cancel request id {} by user id {}", requestId, userId);
        return mapper.fromRequestToDto(service.cancelRequest(requestId, userId));
    }

}
