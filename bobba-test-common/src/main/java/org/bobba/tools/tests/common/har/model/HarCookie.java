package org.bobba.tools.tests.common.har.model;

import lombok.Data;

@Data
public class HarCookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private String expires;
    private Boolean httpOnly;
    private Boolean secure;

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
