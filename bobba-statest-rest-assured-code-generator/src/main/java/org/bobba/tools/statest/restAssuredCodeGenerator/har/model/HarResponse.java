package org.bobba.tools.statest.restAssuredCodeGenerator.har.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarResponse {

    private Integer status;
    private String statusText;
    private String httpVersion;
    private List<HarHeader> headers = new ArrayList<>();
    private List<HarCookie> cookies = new ArrayList<>();
    private HarContent content;
    private String redirectURL;
    private Integer headersSize;
    private Integer bodySize;
    private Integer _transferSize;
    private String _error;

    @Override
    public String toString() {
        return status + " " + content;
    }
}
