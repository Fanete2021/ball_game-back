package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryEventDTO {
    @JsonProperty("t")
    private long timestamp;

    @JsonProperty("type")
    private String type;

    @JsonProperty("ballId")
    private String ballId;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("hit")
    private Boolean hit;

    public long getTimestamp() { return timestamp; }

    public String getType() { return type; }

    public String getBallId() { return ballId; }

    public String getKind() { return kind; }

    public Boolean getHit() { return hit; }
}