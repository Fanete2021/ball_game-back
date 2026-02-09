package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScoreResponseDTO {
    @JsonProperty("rank")
    private int rank;

    @JsonProperty("best")
    private int best;

    public ScoreResponseDTO(int rank, int best) {
        this.rank = rank;
        this.best = best;
    }
}