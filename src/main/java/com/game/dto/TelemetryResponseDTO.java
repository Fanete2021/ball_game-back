package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryResponseDTO {
    @JsonProperty("ok")
    private boolean ok;

    public TelemetryResponseDTO(boolean ok) {
        this.ok = ok;
    }
}