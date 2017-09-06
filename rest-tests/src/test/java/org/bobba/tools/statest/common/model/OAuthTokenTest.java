package org.bobba.tools.statest.common.model;

import com.jayway.restassured.response.Header;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuthTokenTest {

    private final OAuthToken oAuthToken = new OAuthToken("jack", "aToken", Instant.now());

    @Test
    public void testCreatesOauthHeader() {
        final Header header = oAuthToken.createHeader();

        assertThat(header).isNotNull();
        assertThat(header.getName()).isEqualTo("Authorization");
        assertThat(header.getValue()).isEqualTo("OAuth aToken");
    }

}
