package org.bobba.tools.tests.common.har.model;

import lombok.Data;

@Data
public class HarEntry {
    private String startedDateTime;
    private String time;
    private HarRequest request;
    private HarResponse response;
    private HarCache cache;
    private HarEntryTimings timings;
    private String serverIPAddress;
    private String connection;
    private String pageref;

    @Override
    public String toString() {
        return "HarEntry{" + request.getMethod() + " " + request.getUrl() + " }";
    }

}
