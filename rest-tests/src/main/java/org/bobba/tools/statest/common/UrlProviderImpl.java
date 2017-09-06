package org.bobba.tools.statest.common;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.Validate.notEmpty;

public class UrlProviderImpl implements UrlProvider {

    private final String baseUrl;

    public UrlProviderImpl(String baseUrl) {
        this.baseUrl = notEmpty(StringUtils.stripEnd(baseUrl, "/"), "Base url is empty: %s", baseUrl);
    }

    @Override
    public String getUrl() {
        return baseUrl;
    }

    @Override
    public String getUrl(String contextPath) {
        if (StringUtils.isEmpty(contextPath)) {
            return baseUrl;
        }
        return contextPath.startsWith("/") ? baseUrl + contextPath : baseUrl + "/" + contextPath;
    }

}
