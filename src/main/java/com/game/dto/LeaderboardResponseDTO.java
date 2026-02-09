package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LeaderboardResponseDTO {
    @JsonProperty("items")
    private List<LeaderboardEntryDTO> items;

    public LeaderboardResponseDTO(List<LeaderboardEntryDTO> items) {
        this.items = items;
    }
}