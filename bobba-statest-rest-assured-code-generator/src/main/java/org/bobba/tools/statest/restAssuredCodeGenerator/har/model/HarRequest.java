package org.bobba.tools.statest.restAssuredCodeGenerator.har.model;

import com.google.common.base.Joiner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarRequest {

    private String method;
    private String url;
    private String httpVersion;
    private List<HarHeader> headers = new ArrayList<>();
    private List<HarParam> queryString = new ArrayList<>();
    private List<HarCookie> cookies = new ArrayList<>();
    private Integer headersSize;
    private Integer bodySize;
    private HarPostData postData;

    @Override
    public String toString() {
        return method + " " + url + " (" + Joiner.on(",").join(headers) + ")";
    }
}
