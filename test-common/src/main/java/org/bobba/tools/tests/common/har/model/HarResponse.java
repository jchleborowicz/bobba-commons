package org.bobba.tools.tests.common.har.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarResponse {

    private Integer status;
    private String statusText;
    private String httpVersion;
    private List<HarHeader> headers = new ArrayList<HarHeader>();
    private List<HarCookie> cookies = new ArrayList<HarCookie>();
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
