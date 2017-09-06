package org.bobba.tools.commons.conversion;

import com.google.common.collect.ImmutableMap;
import org.bobba.tools.commons.conversion.AbstractMapConverter;
import org.bobba.tools.commons.conversion.ConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

public class AbstractMapConverterTest {

    private AbstractMapConverter<String, Integer, Long, Boolean> converter;

    @Before
    public void setUp() throws Exception {
        this.converter = new AbstractMapConverter<String, Integer, Long, Boolean>() {
            @Override
            protected Map.Entry<Long, Boolean> convertEntry(Map.Entry<String, Integer> entry) throws Exception {
                final Long key = Long.parseLong(entry.getKey());
                final Boolean value = entry.getValue() != 0;
                return new AbstractMap.SimpleEntry<Long, Boolean>(key, value);
            }
        };
    }

    @Test
    public void convertsCorrectly() throws Exception {
        final Map<Long, Boolean> convertedMap = converter.convert(ImmutableMap.of("66", 0, "77", 1));

        assertThat(convertedMap, notNullValue());
        assertThat(convertedMap.size(), is(2));
        assertThat(convertedMap.get(66L), is(Boolean.FALSE));
        assertThat(convertedMap.get(77L), is(Boolean.TRUE));
    }

    @Test(expected = NumberFormatException.class)
    public void throwsExceptionWhenKeyConvertingError() throws Exception {
        converter.convert(ImmutableMap.of("not and integer", 0));
    }

    @Test
    public void throwsExceptionWhenMappingIntoSameKey() throws Exception {
        final AbstractMapConverter<Integer, String, Integer, String> mapConverter =
                new AbstractMapConverter<Integer, String, Integer, String>() {
                    @Override
                    protected Map.Entry<Integer, String> convertEntry(Map.Entry<Integer, String> entry)
                            throws Exception {
                        return new AbstractMap.SimpleEntry<Integer, String>(entry.getKey() / 2, entry.getValue());
                    }
                };
        try {
            mapConverter.convert(ImmutableMap.of(3, "three", 4, "four", 5, "five", 7, "seven"));
            fail("ConversionException expected");
        } catch (ConversionException e) {
            assertThat(e.getMessage(),
                    is("Same target map key \"2\" created for two source map keys: \"4\" and \"5\""));
        }
    }

    @Test
    public void throwsConversionExceptionWhenConvertEntryMethodReturnsNull() throws Exception {
        final AbstractMapConverter<Integer, String, Integer, String> converter =
                new AbstractMapConverter<Integer, String, Integer, String>() {
                    @Override
                    protected Map.Entry<Integer, String> convertEntry(Map.Entry<Integer, String> entry)
                            throws Exception {
                        return null;
                    }
                };

        try {
            converter.convert(ImmutableMap.of(1, "one"));
            fail("ConversionException expected");
        } catch (ConversionException e) {
            assertThat(e.getMessage(), is("Converted object is null for input 1=one"));
        }
    }

}
