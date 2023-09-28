package ru.practicum.explore.ewm.exceptions;

import ru.practicum.explore.ewm.common.CommonConstant;

import java.time.LocalDateTime;

public class EventDateException extends RuntimeException {
    public EventDateException(LocalDateTime eventDate) {
        super("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate.format(CommonConstant.FORMATTER));
    }
}
