package org.bobba.tools.statest.common.model;

import com.jayway.restassured.response.Header;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public final class OAuthToken implements Serializable {

    private final String username;
    private final String token;
    private final LocalDateTime tokenCreationTime;

    public OAuthToken(String username, String token, LocalDateTime tokenCreationTime) {
        this.username = username;
        this.token = token;
        this.tokenCreationTime = tokenCreationTime;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getTokenCreationTime() {
        return tokenCreationTime;
    }

    public Header createHeader() {
        return new Header("Authorization", "OAuth " + token);
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                ", tokenCreationTime=" + tokenCreationTime +
                '}';
    }
}
