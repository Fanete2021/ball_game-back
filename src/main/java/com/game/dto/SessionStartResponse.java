package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionStartResponse {
    private final String sessionId;
    private final long serverTime;

    public SessionStartResponse(String sessionId, long serverTime) {
        this.sessionId = sessionId;
        this.serverTime = serverTime;
    }

    @JsonProperty("sessionId")
    public String getSessionId() {
        return sessionId;
    }

    @JsonProperty("serverTime")
    public long getServerTime() {
        return serverTime;
    }
}
