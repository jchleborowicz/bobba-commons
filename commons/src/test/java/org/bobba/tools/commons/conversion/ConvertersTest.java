package org.bobba.tools.commons.conversion;

import com.google.common.collect.ImmutableList;
import org.bobba.tools.commons.conversion.ConversionException;
import org.bobba.tools.commons.conversion.Converters;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConvertersTest {

    private SimpleUnidirectionalConverter<Integer, String> keyConverter =
            new SimpleUnidirectionalConverter<Integer, String>() {
                @Override
                public String convert(Integer source) throws Exception {
                    return source.toString();
                }
            };

    private SimpleUnidirectionalConverter<Long, String> valueConverter =
            new SimpleUnidirectionalConverter<Long, String>() {
                @Override
                public String convert(Long source) throws Exception {
                    return source + " " + (source * 2);
                }
            };

    @Test
    public void createsCorrectSameObjectConverter() throws Exception {
        final Object input = new Object();
        final SimpleUnidirectionalConverter<Object, Object> objectObjectSimpleUnidirectionalConverter =
                Converters.sameObjectConverter();

        assertThat(objectObjectSimpleUnidirectionalConverter.convert(input), sameInstance(input));
    }

    @Test
    public void sameObjectConverterReturnsNullWhenNullSource() throws Exception {
        final SimpleUnidirectionalConverter<Object, Object> objectObjectSimpleUnidirectionalConverter =
                Converters.sameObjectConverter();
        assertNull(objectObjectSimpleUnidirectionalConverter.convert(null));
    }

    @Test
    public void exceptionThrownWhenTryingToConvertNullAsRequiredMap() {
        try {
            Converters.convertRequiredMap(null, keyConverter, valueConverter);
            fail("ConversionException expected");
        } catch (ConversionException e) {
            assertThat(e.getMessage(), is("Null source is not allowed"));
        }
    }

    @Test
    public void nullValueReturnedWhenTryingToConvertNullOptionalMap() {
        assertThat(Converters.convertOptionalMap(null, keyConverter, valueConverter), nullValue());
    }

    @Test
    public void correctlyConvertsList() {
        final List<String> result = Converters.convertList(ImmutableList.of(1, 2, 3), keyConverter);
        assertThat(result.size(), is(3));
        assertThat(result, hasItems("1", "2", "3"));

    }
}