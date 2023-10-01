package ru.practicum.explore.ewm.exceptions;

public class GettingStatisticException extends RuntimeException {
    public GettingStatisticException(String message) {
        super("Statistic was not received. " + message);
    }
}
