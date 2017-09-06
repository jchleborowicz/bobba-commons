package org.bobba.tools.tests.common.har.model;

import com.google.common.base.Joiner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarRequest {

    private String method;
    private String url;
    private String httpVersion;
    private List<HarHeader> headers = new ArrayList<HarHeader>();
    private List<HarParam> queryString = new ArrayList<HarParam>();
    private List<HarCookie> cookies = new ArrayList<HarCookie>();
    private Integer headersSize;
    private Integer bodySize;
    private HarPostData postData;

    @Override
    public String toString() {
        return method + " " + url + " (" + Joiner.on(",").join(headers) + ")";
    }
}
