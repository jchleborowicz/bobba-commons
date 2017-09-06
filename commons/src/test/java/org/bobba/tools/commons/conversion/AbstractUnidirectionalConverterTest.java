package org.bobba.tools.commons.conversion;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bobba.tools.commons.conversion.AbstractUnidirectionalConverter;
import org.bobba.tools.commons.conversion.ConversionException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
        assertThat(converter.convert(new TestSourceClass(3)), is(new TestTargetClass("3 3")));
        assertThat(converter.convert(new TestSourceClass(66)), is(new TestTargetClass("66 66")));
    }

    @Test
    public void returnsNullWhenNullSourceForNullAcceptingConverter() throws Exception {
        assertThat(nullAcceptingConverter.convert(null), nullValue());
    }

    @Test
    public void returnsExpectedValueWhenNullSourceForNullAcceptingConverter() throws Exception {
        assertThat(nullAcceptingConverterReturningHello.convert(null), is(new TestTargetClass("Hello")));
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

    private static final class TestTargetClass {
        private String text;

        public TestTargetClass() {
        }

        public TestTargetClass(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TestTargetClass that = (TestTargetClass) o;

            return new EqualsBuilder()
                    .append(text, that.text)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(text)
                    .toHashCode();
        }
    }
}