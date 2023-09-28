package ru.practicum.explore.ewm.exceptions;

public class GettingStatisticException extends RuntimeException {
    public GettingStatisticException() {
        super("Statistic was not received");
    }
}
