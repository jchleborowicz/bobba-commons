package org.bobba.tools.tests.common.har.model;

import lombok.Data;

@Data
public class HarContent {
    private Integer size;
    private String mimeType;
    private String text;
    private String encoding;
    private Integer compression;

    @Override
    public String toString() {
        return (mimeType == null ? "" : mimeType + " ") + text;
    }
}
