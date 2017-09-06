package org.bobba.tools.commons.conversion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractUnidirectionalConverterTest {

    private final AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass> converter = createConverter(false);
    private final AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass> nullAcceptingConverter = createConverter(true);
    private final AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass> nullAcceptingConverterReturningHello =
            new AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass>(TestSourceClass.class, TestTargetClass.class, true,
                    new TestTargetClass("Hello")) {
                @Override
                protected void safeConvert(TestSourceClass source,
                                           TestTargetClass target) {
                    internalConvert(source, target);
                }
            };

    private AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass> createConverter(final boolean nullSourceAllowed) {
        return new AbstractUnidirectionalConverter<TestSourceClass, TestTargetClass>(TestSourceClass.class, TestTargetClass.class,
                nullSourceAllowed) {
            @Override
            protected void safeConvert(TestSourceClass source, TestTargetClass target) {
                internalConvert(source, target);
            }
        };
    }

    private void internalConvert(TestSourceClass source,
                                 TestTargetClass target) {
        target.setText(source.getNumber() + " " + source.getNumber());
    }

    @Test
    public void correctlyConvertsNotNullValues() throws Exception {
        assertThat(converter.convert(new TestSourceClass(3))).isEqualTo(new TestTargetClass("3 3"));
        assertThat(converter.convert(new TestSourceClass(66))).isEqualTo(new TestTargetClass("66 66"));
    }

    @Test
    public void returnsNullWhenNullSourceForNullAcceptingConverter() throws Exception {
        assertThat(nullAcceptingConverter.convert(null)).isNull();
    }

    @Test
    public void returnsExpectedValueWhenNullSourceForNullAcceptingConverter() throws Exception {
        assertThat(nullAcceptingConverterReturningHello.convert(null)).isEqualTo(new TestTargetClass("Hello"));
    }

    @Test(expected =  ConversionException.class)
    public void throwsExceptionWhenNullSource() throws Exception {
        converter.convert(null);
    }

    private static final class TestSourceClass {
        private final int number;

        public TestSourceClass(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class TestTargetClass {

        private String text;

    }
}
