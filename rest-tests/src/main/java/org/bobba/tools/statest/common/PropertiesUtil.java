package org.bobba.tools.statest.common;

import org.bobba.tools.tests.common.utils.VariableReplace;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang3.Validate.notNull;

public final class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {
    }

    static Properties readPropertiesFromClasspath(String propertiesFileName) {
        final InputStream inputStream = TestUrls.class.getResourceAsStream(propertiesFileName);
        if (inputStream == null) {
            throw new RuntimeException("Properties file does not exist: " + propertiesFileName);
        }
        final Properties readedProperties = loadPropertiesFromInputStream(inputStream);
        try {
            final Properties result = replacePlaceholders(readedProperties);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(result.toString());
            }
            return result;
        } catch (RuntimeException e) {
            throw new RuntimeException("Exception when reading property file: " + propertiesFileName, e);
        }
    }

    private static Properties replacePlaceholders(Properties properties) {
        final Properties result = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result.put(entry.getKey(), replacePlaceholders(entry.getValue(), properties));
        }
        return result;
    }

    private static Object replacePlaceholders(Object value, Properties properties) {
        if (value instanceof String) {
            return replacePlaceholders((String) value, properties);
        } else {
            return value;
        }
    }

    private static Object replacePlaceholders(final String value, final Properties properties) {
        return VariableReplace.replaceVariables(value, (String variableText) -> {
            notNull(variableText, "Variable name cannot be null");
            if (!properties.containsKey(variableText)) {
                throw new RuntimeException("Variable \"" + variableText + "\" does not exist");
            }
            return StringUtils.defaultString(properties.getProperty(variableText));
        });
    }

    private static Properties loadPropertiesFromInputStream(InputStream inputStream) {
        try {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
