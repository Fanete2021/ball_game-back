package com.game.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionStartRequest {
    private final String nickname;
    private final String clientVersion;

    @JsonCreator
    public SessionStartRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("clientVersion") String clientVersion) {
        this.nickname = nickname;
        this.clientVersion = clientVersion;
    }

    public String getNickname() {
        return nickname;
    }

    public String getClientVersion() {
        return clientVersion;
    }
}
