package org.bobba.tools.statest.common;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeadersBuilder {

    private final List<Header> headers = new ArrayList<>();

    private HeadersBuilder() {
    }

    public static HeadersBuilder newInstance() {
        return new HeadersBuilder();
    }

    public HeadersBuilder withHeader(String name, Object value) {
        return withHeader(name, Objects.toString(value));
    }

    public HeadersBuilder withHeader(String name, String value) {
        headers.add(new Header(name, value));
        return this;
    }

    public HeadersBuilder withHeaders(Headers headers) {
        this.headers.addAll(headers.asList());
        return this;
    }

    public Headers build() {
        return new Headers(headers);
    }
}
