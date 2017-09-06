package org.bobba.tools.commons.conversion;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(converter.convertForward("33")).isEqualTo(33);
    }

    @Test
    public void correctlyConvertsBackward() throws Exception {
        assertThat(converter.convertBackward(34)).isEqualTo("34");
    }

    @Test
    public void reversedConverterCorrectlyConvertsForward() throws Exception {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.convertForward(35)).isEqualTo("35");
    }

    @Test
    public void reversedConverterCorrectlyConvertsBackward() throws Exception {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.convertBackward("36")).isEqualTo(36);
    }

    @Test
    public void reversionOfReversedConverterIsOriginalConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversed = converter.getReversedConverter();
        assertThat(reversed.getReversedConverter()).isSameAs(converter);
    }

    @Test
    public void forwardConverterConvertsCorrectly() throws Exception {
        final SimpleUnidirectionalConverter<String, Integer> forwardConverter = converter.getForwardConverter();
        assertThat(forwardConverter.convert("78")).isEqualTo(78);
    }

    @Test
    public void backwardConverterConvertsCorrectly() throws Exception {
        final SimpleUnidirectionalConverter<Integer, String> backwardConverter = converter.getBackwardConverter();
        assertThat(backwardConverter.convert(79)).isEqualTo("79");
    }

    @Test
    public void reversedForwardConverterIsOriginalBackwardConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.getForwardConverter()).isSameAs(converter.getBackwardConverter());
    }

    @Test
    public void reversedBackwardConverterIsOriginalForwardConverter() {
        final SimpleBidirectionalConverter<Integer, String> reversedConverter = converter.getReversedConverter();
        assertThat(reversedConverter.getBackwardConverter()).isSameAs(converter.getForwardConverter());
    }
}
