package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScoreRequestDTO {
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("score")
    private int score;

    public String getSessionId() { return sessionId; }

    public int getScore() { return score; }
}