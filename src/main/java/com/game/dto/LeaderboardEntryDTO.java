package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LeaderboardEntryDTO {
    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("score")
    private int score;

    @JsonProperty("when")
    private long when;

    public LeaderboardEntryDTO(String nickname, int score, long when) {
        this.nickname = nickname;
        this.score = score;
        this.when = when;
    }
}