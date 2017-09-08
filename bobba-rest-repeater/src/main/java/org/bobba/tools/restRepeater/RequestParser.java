package org.bobba.tools.restRepeater;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

@Component
public class RequestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParser.class);

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    private static final String FULL_TIME_PATTERN = "\\d{2}:\\d{2}:\\d{2}[-+]\\d{4}";
    private static final String UID_PATTERN = "uid:[a-f0-9-]{8}-[a-f0-9-]{4}-[a-f0-9-]{4}-[a-f0-9-]{4}-[a-f0-9-]{12}";
    private static final String IP_PATTERN = "ip:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    private static final String CONTEXT_PATH_PATTERN = "[/a-zA-Z0-9\\?=\\-_&]+";
    private static final String EXAMPLE_FIRST_LINE =
            "2015-02-24 07:28:06-0500 uid:e812a2a0-6fdd-4b00-8fe9-18ecb5fbbe00 ip:127.0.0.1 apikey:-- rt:26.7ms"
                    + " 200 GET /adapter/api/v2/players/self/services";

    public Request parseRequest(String requestString) {
        logRequest(requestString);

        final BufferedReader input = new BufferedReader(new StringReader(requestString));

        try {
            final String firstLine = input.readLine();
            final Request result = parseFirstLine(firstLine);
            final String secondLine = input.readLine();
            if (!"-- Request --".equals(secondLine)) {
                throw new RuntimeException("Second line of the request should be equal to: -- Request --");
            }
            String nextLine = input.readLine();
            while (nextLine != null && nextLine.contains(":")) {
                final String[] header = nextLine.trim().split(":", 2);
                result.getHeaders().put(header[0], header[1]);
                nextLine = input.readLine();
            }

            final StringBuilder requestBody = new StringBuilder();

            while (nextLine != null && !"-- Response --".equals(nextLine)) {
                if (requestBody.length() > 0) {
                    requestBody.append("\n");
                }
                requestBody.append(nextLine);
                nextLine = input.readLine();
            }

            if (StringUtils.isNotBlank(requestBody.toString())) {
                result.setBody(requestBody.toString());
            }
            if (!"-- Response --".equals(nextLine)) {
                throw new RuntimeException("Unexpected request line:\n" + nextLine);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Request parseFirstLine(String firstLine) {
        final String[] split = firstLine.split("\\s+");
        if (split.length != 9) {
            throw new RuntimeException("First line should have 8 tokens: date, time, uid, apiKey, runTime, returnCode, "
                    + "method and url.\nSample first line:\n" + EXAMPLE_FIRST_LINE + "\nActual first line has "
                    + split.length + " tokens:\n" + firstLine);
        }
        final String date = split[0];
        final String time = split[1];
        final String uid = split[2];
        final String ip = split[3];
        final String apiKey = split[4];
        final String runTime = split[5];
        final String returnCode = split[6];
        final String method = split[7];
        final String contextPath = split[8];

        matches(date, DATE_PATTERN);
        matches(time, FULL_TIME_PATTERN);
        matches(uid, UID_PATTERN);
        matches(ip, IP_PATTERN);
        matches(apiKey, "apikey:--");
        matches(runTime, "rt:[\\d+\\.,]+m?s");
        matches(returnCode, "\\d+");
        matches(method, "(POST|GET|PUT)");
        matches(contextPath, CONTEXT_PATH_PATTERN);

        final Request request = new Request();

        request.setUid(uid);
        request.setHost(ip);
        request.setHttpMethod(method);
        request.setPath(contextPath);

        return request;
    }

    public static String matches(String text, String pattern) {
        return matches(text, pattern, "Matching error");
    }

    public static String matches(String text, String pattern, String errorMessage) {
        if (!text.matches(pattern)) {
            throw new RequestParserException(errorMessage + ", text: " + text + ", pattern: " + pattern);
        }
        return text;
    }

    private void logRequest(String text) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsing request:\n" + appendLineNumbers(text));
        }
    }

    public static String appendLineNumbers(String text) {
        final String[] lines = text.split("\n");
        final int length = lines.length;
        final int places = digitsCount(length);
        int lineNumber = 1;
        final StringBuilder result = new StringBuilder();
        for (String line : lines) {
            final String lineNumberString = StringUtils.leftPad(Integer.toString(lineNumber), places, ' ');
            result.append(lineNumberString)
                    .append(":")
                    .append(line)
                    .append("\n");
            lineNumber++;
        }
        return result.toString();
    }

    private static int digitsCount(int length) {
        return new BigDecimal(Math.log10(length)).setScale(0, BigDecimal.ROUND_UP).intValue();
    }

}
