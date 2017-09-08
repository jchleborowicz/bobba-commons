package org.bobba.tools.commandLine;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RestRepeater {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestRepeater.class);

    private final RequestParser parser;

    public RestRepeater(RequestParser parser) {
        this.parser = parser;
    }

    public void sendFileContent(String fileName, String targetHost) {
        final String requestString = readInputFileFromClasspath(fileName);

        parseAndSend(requestString, targetHost);
    }

    public void parseAndSend(String requestString, String targetHost) {
        final Request request = parser.parseRequest(requestString);
        update(request, targetHost);
        send(request);
    }

    private void update(Request request, String targetHost) {
        request.setHost(targetHost);
        removeIgnoreCase(request.getHeaders(), HTTP.TRANSFER_ENCODING);
        removeIgnoreCase(request.getHeaders(), HTTP.CONTENT_LEN);
    }

    private void removeIgnoreCase(Map<String, ?> map, String keyToRemove) {
        final Set<String> keys = new HashSet<>(map.keySet());
        for (String key : keys) {
            if (key.equalsIgnoreCase(keyToRemove)) {
                map.remove(key);
            }
        }
    }

    private void send(Request request) {
        final ClientRequest clientRequest = createClientRequest(request);

        try {
            final ClientResponse<String> response = clientRequest.httpMethod(request.getHttpMethod(), String.class);
            final String entity = response.getEntity();

            LOGGER.info("REST call finished\nRequest URL: " + clientRequest.getUri()
                    + "\nRequest method: " + clientRequest.getHttpMethod()
                    + "\nRequest headers:\n" + headersToString(clientRequest.getHeaders())
                    + "\nRequest body:\n" + clientRequest.getBody()
                    + "\n\nResponse status: " + response.getStatus()
                    + "\nResponse headers: " + headersToString(response.getResponseHeaders())
                    + "\nResponse body:\n" + entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String headersToString(MultivaluedMap<String, String> headers) {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            result.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return result.toString();
    }

    private ClientRequest createClientRequest(Request request) {
        String url = "http://" + request.getHost();

        url += request.getPath();

        final ClientRequest result = new ClientRequest(url);
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            result.header(header.getKey(), header.getValue());
        }

        result.setHttpMethod(request.getHttpMethod());

        if (StringUtils.isNotBlank(request.getBody())) {
            result.body(MediaType.APPLICATION_JSON_TYPE, request.getBody());
        }

        return result;
    }

    private static String readInputFileFromClasspath(String fileName) {
        final InputStream inputResource = RestRepeater.class.getResourceAsStream(fileName);
        return readResource(inputResource);
    }

    private static String readResource(InputStream inputResource) {
        Preconditions.checkNotNull(inputResource, "Cannot find input");
        try {
            return IOUtils.toString(inputResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
