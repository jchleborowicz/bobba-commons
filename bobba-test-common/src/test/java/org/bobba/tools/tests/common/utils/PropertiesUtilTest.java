package org.bobba.tools.tests.common.utils;

import org.bobba.tools.tests.common.utils.PropertiesUtil;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class PropertiesUtilTest {

    @Test
    public void correctlyReadsNestedProperties() {
        final Properties properties = PropertiesUtil.readPropertiesFromClasspath("/test.properties");

        assertEquals(3, properties.size());
        assertEquals("abc", properties.getProperty("nested.prop"));
        assertEquals("3434", properties.get("propA"));
        assertEquals("nested-abc-prop", properties.get("propB"));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionWhenPlaceholderPropertyDoesNotExist() {
        PropertiesUtil.readPropertiesFromClasspath("/test-non-existent-property.properties");
    }

}
