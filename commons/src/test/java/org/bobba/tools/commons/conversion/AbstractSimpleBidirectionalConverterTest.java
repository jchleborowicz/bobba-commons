package org.bobba.tools.commons.conversion;

import org.bobba.tools.commons.conversion.AbstractSimpleBidirectionalConverter;
import org.bobba.tools.commons.conversion.SimpleBidirectionalConverter;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class AbstractSimpleBidirectionalConverterTest {

    private AbstractSimpleBidirectionalConverter<String, Integer> converter =
            new AbstractSimpleBidirectionalConverter<String, Integer>() {
                @Override
                protected Integer safeConvertForward(String source) throws Exception {
                    return Integer.parseInt(source);
                }

                @Override
                protected String safeConvertBackward(Integer source) throws Exception {
                    return source.toString();
                }
            };

    @Test
    public void correctlyConvertsForward() throws Exception {
        assertThat(converter.convertForward("33"), is(33));
    }

    @Test
    public void correctlyConvertsBackward() throws Exception {
        assertThat(converter.convertBackward(34), is("34"));
    }

    @Test
    public void reversedConverterCorrectlyConvertsForward() throws Exception {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.convertForward(35), is("35"));
    }

    @Test
    public void reversedConverterCorrectlyConvertsBackward() throws Exception {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.convertBackward("36"), is(36));
    }

    @Test
    public void reversionOfReversedConverterIsOriginalConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversed = converter.getReversedConverter();
        assertThat(reversed.getReversedConverter(),
                sameInstance((SimpleBidirectionalConverter<String, Integer>) converter));
    }

    @Test
    public void forwardConverterConvertsCorrectly() throws Exception {
        final SimpleUnidirectionalConverter<String, Integer> forwardConverter = converter.getForwardConverter();
        assertThat(forwardConverter.convert("78"), is(78));
    }

    @Test
    public void backwardConverterConvertsCorrectly() throws Exception {
        final SimpleUnidirectionalConverter<Integer, String> backwardConverter = converter.getBackwardConverter();
        assertThat(backwardConverter.convert(79), is("79"));
    }

    @Test
    public void reversedForwardConverterIsOriginalBackwardConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.getForwardConverter(), sameInstance(converter.getBackwardConverter()));
    }

    @Test
    public void reversedBackwardConverterIsOriginalForwardConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.getBackwardConverter(), sameInstance(converter.getForwardConverter()));
    }
}
