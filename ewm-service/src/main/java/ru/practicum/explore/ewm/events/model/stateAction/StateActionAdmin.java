package ru.practicum.explore.ewm.events.model.stateAction;

import lombok.Getter;

@Getter
public enum StateActionAdmin {
    PUBLISH_EVENT("publishing"),
    SEND_TO_REVISION("sending to revision"),
    REJECT_EVENT("rejecting");
    private String action;

    StateActionAdmin(String action) {
        this.action = action;
    }
}
