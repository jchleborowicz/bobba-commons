package org.bobba.tools.commons.conversion;

import org.bobba.tools.commons.conversion.AbstractSimpleUnidirectionalConverter;
import org.bobba.tools.commons.conversion.ConversionException;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AbstractSimpleUnidirectionalConverterTest {

    private AbstractSimpleUnidirectionalConverter<Integer, String> converter =
            new AbstractSimpleUnidirectionalConverter<Integer, String>(Integer.class, String.class) {
                @Override
                protected String safeConvert(Integer source) {
                    return Integer.toString(source * 2);
                }
            };

    private AbstractSimpleUnidirectionalConverter<Integer, String> nullAcceptingConverter =
            new AbstractSimpleUnidirectionalConverter<Integer, String>(Integer.class, String.class, true, "null value") {
                @Override
                protected String safeConvert(Integer source) {
                    return Integer.toString(source * 2);
                }
            };

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenSourceObjectIsNotOfExcpectedClass() throws Exception {
        //noinspection unchecked
        ((SimpleUnidirectionalConverter) converter).convert(3L);
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenSourceObjectIsNull() throws Exception {
        converter.convert(null);
    }

    @Test
    public void doesNotTrowsExceptionForNullAcceptingConverterWhenSourceObjectIsNull() throws Exception {
        assertThat(nullAcceptingConverter.convert(null), is("null value"));
    }

    @Test
    public void properlyConvertsIntegerValues() throws Exception {
        assertThat(converter.convert(3), is("6"));
        assertThat(converter.convert(7), is("14"));
    }

    @Test
    public void returnsCorrectNullSorceAllowed() {
        assertThat(converter.isNullSourceAllowed(), is(false));
        assertThat(nullAcceptingConverter.isNullSourceAllowed(), is(true));
    }
}
