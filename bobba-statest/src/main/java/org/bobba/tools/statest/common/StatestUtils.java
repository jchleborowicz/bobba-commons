package org.bobba.tools.statest.common;

import org.bobba.tools.statest.utils.StatestCommonUtils;
import com.jayway.restassured.response.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.Validate.notNull;

public final class StatestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatestUtils.class);

    private StatestUtils() {
    }

    public static Headers jsonHeaders() {
        return HeadersBuilder.newInstance()
                .withHeader("accept", "application/json")
                .withHeader("Content-Type", "application/json")
                .withHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36")
                .build();
    }

    public static void logRestCallStarted(int callStackRelativePosition) {
        if (LOGGER.isDebugEnabled()) {
            final String message = StatestCommonUtils.createCodePointer(callStackRelativePosition - 1);
            LOGGER.debug(StringUtils.rightPad("******** REST CALL *****", message.length(), '*'));
            LOGGER.debug(message);
        }
    }

    public static String defaultObjectId(Class<?> aClass) {
        notNull(aClass, "Cannot determine default object id for empty class");
        return aClass.getName();
    }

}
