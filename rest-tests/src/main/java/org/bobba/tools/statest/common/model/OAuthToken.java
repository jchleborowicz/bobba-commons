package org.bobba.tools.statest.common.model;

import com.jayway.restassured.response.Header;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

@Value
public final class OAuthToken implements Serializable {

    private final String username;
    private final String token;
    private final Instant tokenCreationTime;

    public OAuthToken(String username, String token, Instant tokenCreationTime) {
        this.username = username;
        this.token = token;
        this.tokenCreationTime = tokenCreationTime;
    }

    public Header createHeader() {
        return new Header("Authorization", "OAuth " + token);
    }

}
