package org.bobba.tools.commons.conversion;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ConvertersTest {

    private SimpleUnidirectionalConverter<Integer, String> keyConverter = Object::toString;

    private SimpleUnidirectionalConverter<Long, String> valueConverter =
            source -> source + " " + (source * 2);

    @Test
    public void createsCorrectSameObjectConverter() throws Exception {
        final Object input = new Object();
        final SimpleUnidirectionalConverter<Object, Object> objectObjectSimpleUnidirectionalConverter =
                Converters.sameObjectConverter();

        assertThat(objectObjectSimpleUnidirectionalConverter.convert(input)).isSameAs(input);
    }

    @Test
    public void sameObjectConverterReturnsNullWhenNullSource() throws Exception {
        final SimpleUnidirectionalConverter<Object, Object> objectObjectSimpleUnidirectionalConverter =
                Converters.sameObjectConverter();
        assertThat(objectObjectSimpleUnidirectionalConverter.convert(null)).isNull();
    }

    @Test
    public void exceptionThrownWhenTryingToConvertNullAsRequiredMap() {
        try {
            Converters.convertRequiredMap(null, keyConverter, valueConverter);
            fail("ConversionException expected");
        } catch (ConversionException e) {
            assertThat(e.getMessage()).isEqualTo("Null source is not allowed");
        }
    }

    @Test
    public void nullValueReturnedWhenTryingToConvertNullOptionalMap() {
        assertThat(Converters.convertOptionalMap(null, keyConverter, valueConverter)).isNull();
    }

    @Test
    public void correctlyConvertsList() {
        final List<String> result = Converters.convertList(ImmutableList.of(1, 2, 3), keyConverter);

        assertThat(result).containsExactly("1", "2", "3");
    }

}
