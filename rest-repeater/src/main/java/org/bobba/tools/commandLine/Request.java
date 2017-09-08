package org.bobba.tools.commandLine;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String uid;
    private String host;
    private String httpMethod;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUid() {
        return uid;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
