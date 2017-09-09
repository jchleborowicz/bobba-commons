package org.bobba.tools.statest.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bobba.tools.statest.common.RestAssuredCodeGenerator;
import org.bobba.tools.tests.common.BobbaTestUtils;
import org.bobba.tools.tests.common.har.HarDeserializer;
import org.bobba.tools.tests.common.har.model.HarContent;
import org.bobba.tools.tests.common.har.model.HarCookie;
import org.bobba.tools.tests.common.har.model.HarEntry;
import org.bobba.tools.tests.common.har.model.HarHeader;
import org.bobba.tools.tests.common.har.model.HarLog;
import org.bobba.tools.tests.common.har.model.HarModel;
import org.bobba.tools.tests.common.har.model.HarPostData;
import org.bobba.tools.tests.common.har.model.HarRequest;
import org.bobba.tools.tests.common.har.model.HarResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;

public class RestTestGenerator {

    private static final String INDENT = "    ";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern urlPattern = Pattern.compile("https?://[a-zA-Z0-9.\\-]+/(.*)");

    private final PrintStream outputStream;
    private int indentLevel;
    private int testIndex = 1;
    private final Set<String> headersToSkip = new HashSet<>();
    private final Set<String> cookiesToSkip = new HashSet<>();
    private final Map<String, Integer> methodCounters = Maps.newHashMap();

    public RestTestGenerator(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("ERROR: Please provide HAR file name as a run parameter.");
            System.exit(-1);
        }
        final String generatedFilesBase = "/tmp/test";

        final List<HarEntry> entries = getHarEntriesFromFile(args[0]);

        final RestTestGenerator restTestGenerator = createRestTestGenerator();

        entries.forEach(entry -> {
            restTestGenerator.appendTest(entry, generatedFilesBase);
        });
    }

    private static List<HarEntry> getHarEntriesFromFile(String harInputFileName) {
        final HarLog harLog = readHarLog(harInputFileName);

        return excludeSkippedEntries(harLog.getEntries());
    }

    private static RestTestGenerator createRestTestGenerator() {
        final RestTestGenerator restTestGenerator = new RestTestGenerator(System.out);

        restTestGenerator.skipHeaders("Authorization", "Origin", "Host", "Accept-Language", "User-Agent",
                "Content-Type", "Referer", "x-requested-with", "cookie", "Accept", "Accept-Encoding",
                "Connection", "Content-Length", "Upgrade-Insecure-Requests", "Cache-Control");
        restTestGenerator.skipCookies("__utma", "__utmz", "__utmt", "_gat", "sessionTimeout", "__utma",
                "__utmb", "__utmc", "transaction", "_ga", "sessionTimeout1");
        return restTestGenerator;
    }

    private static HarLog readHarLog(String harInputFileName) {
        final HarModel har = HarDeserializer.deserializeFile(harInputFileName);
        return har.getLog();
    }

    private static List<HarEntry> excludeSkippedEntries(List<HarEntry> entries) {
        return entries.stream()
                .filter(RestTestGenerator::shouldBeIncluded)
                .collect(toList());
    }

    private static boolean shouldBeIncluded(HarEntry entry) {
        return !shouldBeSkipped(entry);
    }

    private static boolean shouldBeSkipped(HarEntry entry) {
        final String url = entry.getRequest().getUrl();

        return url.endsWith(".css") || url.startsWith("https://fonts.gstatic.com/s/opensans")
                || url.startsWith("https://fonts.googleapis.com/css")
                || url.endsWith(".js")
                || url.contains(".js?")
                || url.endsWith(".png")
                || url.endsWith(".jpg")
                || url.endsWith(".svg")
                || url.startsWith("http://www.google-analytics.com")
                || url.contains(".woff2?")
                || url.endsWith(".woff2")
                || url.contains(".ttf?")
                || url.endsWith(".getMessageDictionary.json")
                || url.endsWith("/dict.en.json")
                || url.endsWith("/home.htm")
                || url.endsWith("/home.html");
    }

    private void skipCookies(String... cookieNames) {
        for (String cookieName : cookieNames) {
            skipCookie(cookieName);
        }
    }

    private void skipHeaders(String... headerNames) {
        for (String headerName : headerNames) {
            skipHeader(headerName);
        }
    }

    private void skipHeader(String headerName) {
        headersToSkip.add(headerName.toUpperCase());
    }

    private void skipCookie(String cookieName) {
        cookiesToSkip.add(cookieName.toUpperCase());
    }

    public void appendTest(HarEntry entry, String generatedFilesBase) {
        increaseIndent();
        line("@RestTest(order = " + testIndex + ")");
        testIndex++;
        final List<String> params = new ArrayList<>();
        boolean hasAuthorization = hasHeader(entry, "Authorization");
        String methodName = createTestMethodName(entry);
        if (hasAuthorization) {
            params.add("@OAuthAuthorization OAuthToken oauthToken");
        }
        line("public void " + methodName + "(" + Joiner.on(", ").join(params) + ") {");
        increaseIndent();

        printExpectSection(entry);
        printGivenSection(entry.getRequest(), hasAuthorization, generatedFilesBase + methodName + ".json");
        writeResponse(entry.getResponse(), generatedFilesBase + methodName + "-response.json");
        printWhenSection(entry.getRequest());

        decreaseIndent();
        line("}");
        decreaseIndent();
        line("");
    }

    private String createTestMethodName(HarEntry entry) {
        final HarRequest request = entry.getRequest();
        final String url = request.getUrl();
        final Matcher matcher = urlPattern.matcher(url);
        if (matcher.find()) {
            String methodName = makeMethodName(matcher.group(1));
            if (methodCounters.containsKey(methodName)) {
                final Integer methodCounter = methodCounters.get(methodName);
                methodCounters.put(methodName, methodCounter + 1);
                methodName += methodCounter;
            } else {
                methodCounters.put(methodName, 1);
            }
            return methodName;
        } else {
            throw new RuntimeException("Incorrect request url: " + url);
        }
    }

    private String makeMethodName(String urlText) {
        final StringBuilder result = new StringBuilder();

        boolean uppercase = false;
        for (char c : urlText.toCharArray()) {
            if (c == '?') {
                break;
            }
            if (CharUtils.isAsciiAlphanumeric(c)) {
                result.append(uppercase ? Character.toUpperCase(c) : c);
                uppercase = CharUtils.isAsciiNumeric(c);
            } else {
                uppercase = true;
            }
        }
        return notEmpty(result.toString(), "Empty method name for url: %s", urlText);
    }

    private void writeResponse(HarResponse response, String outputFileName) {
        final HarContent content = response.getContent();
        if (content != null && StringUtils.isNotBlank(content.getText())) {
            final String text;
            if ("application/json".equals(content.getMimeType())) {
                text = formatJson(content.getText());
            } else {
                text = content.getText();
            }
            try {
                FileUtils.write(new File(outputFileName), text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private boolean hasHeader(HarEntry entry, String headerName) {
        final List<HarHeader> headers = entry.getRequest().getHeaders();

        return headers.stream()
                .anyMatch(harHeader -> headerName.equalsIgnoreCase(harHeader.getName()));
    }

    private void printGivenSection(HarRequest request, boolean hasAuthorization, String bodyFileName) {
        line(".given()");

        increaseIndent();
        for (HarHeader harHeader : request.getHeaders()) {
            final String headerName = harHeader.getName();
            if (!headersToSkip.contains(headerName.toUpperCase())) {
                line(".header(" + stringLiteral(headerName) + ", " + stringLiteral(harHeader.getValue()) + ")");
            }
        }
        if (hasAuthorization) {
            line(".header(oauthToken.createHeader())");
        }
        for (HarCookie harCookie : request.getCookies()) {
            final String cookieName = harCookie.getName();
            if (!cookiesToSkip.contains(cookieName.toUpperCase())) {
                line(".cookie(" + stringLiteral(cookieName) + ", " + stringLiteral(harCookie.getValue()) + ")");
            }
        }

        final HarPostData postData = request.getPostData();
        if (postData != null) {
            if (postData.getMimeType().equals("application/json")) {
                final File file = new File(bodyFileName);
                try {
                    FileUtils.write(file, formatJson(postData.getText()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                line(".body(loadFromFile(\"tests/generated/" + file.getName() + "\"))");
            } else {
                line(".body(" + stringLiteral(postData.getText()) + ")");
            }
        }
        decreaseIndent();
    }

    private String formatJson(String json) {
        final Object map;
        try {
            if (json.trim().charAt(0) == '[') {
                map = OBJECT_MAPPER.readValue(json, new TypeReference<List<Map>>() {
                });
            } else {
                map = OBJECT_MAPPER.readValue(json, Map.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return BobbaTestUtils.toPrettyJson(map);
    }

    private void printWhenSection(HarRequest request) {
        line(".when()");

        increaseIndent();
        line("." + request.getMethod().toLowerCase() + "(" + encodeUrl(request.getUrl()) + ");");
        decreaseIndent();
    }

    private String encodeUrl(String url) {
        final String esaUrl = "http://devplay.kylottery.com/";
        if (url.startsWith(esaUrl)) {
            return "esaUrl(\"" + url.substring(esaUrl.length()) + "\")";
        }
        return stringLiteral(url);
    }

    private String stringLiteral(String string) {
        return "\"" + StringEscapeUtils.escapeJava(string) + "\"";
    }

    private void printExpectSection(HarEntry entry) {
        line("expect()");

        increaseIndent();
        final HarResponse response = entry.getResponse();
        line(".statusCode(" + response.getStatus() + ")");
        for (HarHeader harHeader : response.getHeaders()) {
            line(".header(" + stringLiteral(harHeader.getName()) + ", " + stringLiteral(harHeader.getValue()) + ")");
        }
        final HarContent content = response.getContent();
        if (content != null && StringUtils.isNotEmpty(content.getText())) {
            if (content.getMimeType().startsWith("application/json")) {
                final String restAssertions = RestAssuredCodeGenerator.generateAssertionsForJson(content.getText());
                final String[] lines = restAssertions.split("\n");
                for (String line : lines) {
                    line(line);
                }
            } else {
                final String[] lines = content.getText().split("\n");
                for (String line : lines) {
                    line("//" + line);
                }
            }
        }
        decreaseIndent();
    }

    public void increaseIndent() {
        indentLevel++;
    }

    public void decreaseIndent() {
        decreaseIndent(1);
    }

    @SuppressWarnings("SameParameterValue")
    private void decreaseIndent(int indent) {
        indentLevel -= Math.abs(indent);
    }

    private void line(String line) {
        for (int i = 0; i < indentLevel; i++) {
            outputStream.append(INDENT);
        }
        outputStream.append(line);
        outputStream.append("\n");
    }

}
