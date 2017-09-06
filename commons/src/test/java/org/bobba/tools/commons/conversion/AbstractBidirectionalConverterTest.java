package org.bobba.tools.commons.conversion;

import org.bobba.tools.commons.TestIntegerHolder;
import org.bobba.tools.commons.TestStringHolder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractBidirectionalConverterTest {

    private BidirectionalConverter<TestIntegerHolder, TestStringHolder> converter =
            new AbstractBidirectionalConverter<TestIntegerHolder, TestStringHolder>(TestIntegerHolder.class,
                    TestStringHolder.class) {
                @Override
                protected void safeConvertForward(TestIntegerHolder source, TestStringHolder target) {
                    target.setText(Integer.toString(source.getNumber()));
                }

                @Override
                protected void safeConvertBackward(TestStringHolder source, TestIntegerHolder target) {
                    target.setNumber(Integer.parseInt(source.getText()));
                }
            };

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenForwardConversionNullSource() throws Exception {
        converter.convertForward(null);
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenBackwardConversionNullSource() throws Exception {
        converter.convertBackward(null);
    }

    @Test
    public void correctlyConvertsForward() throws Exception {
        assertThat(converter.convertForward(new TestIntegerHolder(3))).isEqualTo(new TestStringHolder("3"));
    }

    @Test
    public void correctlyUpdatesForward() throws Exception {
        final TestStringHolder target = new TestStringHolder();
        converter.convertForward(new TestIntegerHolder(38), target);
        assertThat(target).isEqualTo(new TestStringHolder("38"));
    }

    @Test
    public void correctlyConvertsBackward() throws Exception {
        assertThat(converter.convertBackward(new TestStringHolder("77"))).isEqualTo(new TestIntegerHolder(77));
    }

    @Test
    public void correctlyUpdatesBackward() throws Exception {
        final TestIntegerHolder target = new TestIntegerHolder();
        converter.convertBackward(new TestStringHolder("8734"), target);
        assertThat(target).isEqualTo(new TestIntegerHolder(8734));
    }

    @Test(expected = NumberFormatException.class)
    public void throwsExceptionWhenBackwardConversionThrowsException() throws Exception {
        converter.convertBackward(new TestStringHolder("not an integer"));
    }

    @Test
    public void revertingReversedConverterReturnsOriginalConverter() {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        assertThat(reversedConverter.getReversedConverter()).isSameAs(converter);
    }

    @Test
    public void testReversedForwardConvertedIsOriginalBackwardConverter() {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        assertThat(reversedConverter.getForwardConverter()).isSameAs(converter.getBackwardConverter());
    }

    @Test
    public void testReversedBackwardConverterIsOriginalForwardConverter() {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        assertThat(reversedConverter.getBackwardConverter()).isSameAs(converter.getForwardConverter());
    }

    @Test
    public void reversedConverterCorrectlyConvertsForward() throws Exception {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        assertThat(reversedConverter.convertForward(new TestStringHolder("37"))).isEqualTo(new TestIntegerHolder(37));
    }

    @Test
    public void setReversedConverterCorrectlyUpdatesForward() throws Exception {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        final TestIntegerHolder result = new TestIntegerHolder();
        reversedConverter.convertForward(new TestStringHolder("38"), result);
        assertThat(result).isEqualTo(new TestIntegerHolder(38));
    }

    @Test
    public void reversedConverterCorrectlyConvertsBackward() throws Exception {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        assertThat(reversedConverter.convertBackward(new TestIntegerHolder(39))).isEqualTo(new TestStringHolder("39"));
    }

    @Test
    public void setReversedConverterCorrectlyUpdatesBackward() throws Exception {
        final BidirectionalConverter<TestStringHolder, TestIntegerHolder> reversedConverter =
                converter.getReversedConverter();
        final TestStringHolder result = new TestStringHolder();
        reversedConverter.convertBackward(new TestIntegerHolder(40), result);
        assertThat(result).isEqualTo(new TestStringHolder("40"));
    }

}
