package org.bobba.tools.statest.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUrls.class);

    public static final UrlProvider SOME_URL;

    static {
        SOME_URL = new UrlProviderImpl("http://localhost:8080");
        logUrls();
    }

    private static void logUrls() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Test URLs initialized as follows:"
                            + "\nSOME_URL: " + SOME_URL.getUrl()
            );
        }
    }

    private static String removeTailingSlashes(String text) {
        return StringUtils.stripEnd(text, "/");
    }

    private TestUrls() {
    }

    public static String someUrl(String path) {
        return SOME_URL.getUrl(path);
    }

}
