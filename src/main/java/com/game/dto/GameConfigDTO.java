package com.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameConfigDTO {
    @JsonProperty("maxBalls")
    private int maxBalls = 10;

    @JsonProperty("ballChances")
    private BallChances ballChances = new BallChances();

    @JsonProperty("maxLives")
    private int maxLives = 3;

    @JsonProperty("bonusDurations")
    private BonusDurations bonusDurations = new BonusDurations();

    @JsonProperty("scores")
    private Scores scores = new Scores();

    @JsonProperty("ballSpeed")
    private BallSpeed ballSpeed = new BallSpeed();

    public static class BallChances {
        @JsonProperty("normal")
        private double normal = 0.45;

        @JsonProperty("bad")
        private double bad = 0.40;

        @JsonProperty("doublePoints")
        private double doublePoints = 0.05;

        @JsonProperty("bomb")
        private double bomb = 0.05;

        @JsonProperty("heal")
        private double heal = 0.05;
    }

    public static class BonusDurations {
        @JsonProperty("doublePoints")
        private int doublePoints = 10;
    }

    public static class Scores {
        @JsonProperty("normal")
        private double normal = 1;
    }

    public static class BallSpeed {
        @JsonProperty("min")
        private double min = 0.8;

        @JsonProperty("max")
        private double max = 1.5;
    }
}