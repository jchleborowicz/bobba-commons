package org.bobba.tools.statest.common.model;

import com.jayway.restassured.response.Header;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuthTokenTest {

    private final OAuthToken oAuthToken =
            new OAuthToken("jack", "aToken", LocalDateTime.parse("2017-09-06T21:40:14.038"));

    @Test
    public void testCreatesOauthHeader() {
        final Header header = oAuthToken.createHeader();

        assertThat(header).isNotNull();
        assertThat(header.getName()).isEqualTo("Authorization");
        assertThat(header.getValue()).isEqualTo("OAuth aToken");
    }

}
