package org.bobba.tools.statest.restAssuredCodeGenerator.har;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bobba.tools.statest.restAssuredCodeGenerator.har.model.HarEntry;
import org.bobba.tools.statest.restAssuredCodeGenerator.har.model.HarModel;
import org.bobba.tools.statest.restAssuredCodeGenerator.har.model.HarPostData;
import org.bobba.tools.statest.restAssuredCodeGenerator.har.model.HarRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.lang3.Validate.notNull;

public final class HarDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HarDeserializer.class);

    private HarDeserializer() {
    }

    public static HarModel deserializeFile(String fileName) {
        try {
            return new ObjectMapper().readValue(new File(fileName), HarModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HarModel deserializeResource(String resourceName) {
        final InputStream resourceAsStream = openResource(resourceName);
        try {
            return new ObjectMapper().readValue(resourceAsStream, HarModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                LOGGER.error("Error when closing resource " + resourceName, e);
            }
        }
    }

    private static InputStream openResource(String resourceName) {
        final InputStream resourceAsStream =
                HarDeserializer.class.getClassLoader().getResourceAsStream(resourceName);
        return notNull(resourceAsStream, "Resource %s could not be found", resourceName);
    }

    public static void printEntries(HarModel model) {
        int i = 1;
        for (HarEntry harEntry : model.getLog().getEntries()) {
            final HarRequest request = harEntry.getRequest();
            final HarPostData postData = request.getPostData();
            final String postDataText = postData == null || StringUtils.isEmpty(postData.getText()) ? ""
                    : "\n" + postData.getText();
            System.out.println(i++ + ") Request: " + request + postDataText);
            System.out.println("Response: " + harEntry.getResponse());
        }
    }

}
