package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TelemetryRequestDTO {
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("events")
    private List<TelemetryEventDTO> events;
    public String getSessionId() { return sessionId; }

    public List<TelemetryEventDTO> getEvents() { return events; }
}