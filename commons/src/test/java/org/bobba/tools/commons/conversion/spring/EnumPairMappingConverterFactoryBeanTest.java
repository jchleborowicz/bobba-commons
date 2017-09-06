package org.bobba.tools.commons.conversion.spring;

import com.google.common.collect.ImmutableMap;
import org.bobba.tools.commons.conversion.ConversionException;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class EnumPairMappingConverterFactoryBeanTest {

    private EnumPairMappingConverterFactoryBean<RetentionPolicy, ElementType> factoryBean;

    @Before
    public void setUp() throws Exception {
        factoryBean = new EnumPairMappingConverterFactoryBean<RetentionPolicy, ElementType>();

        factoryBean.setSourceClass(RetentionPolicy.class);
        factoryBean.setTargetClass(ElementType.class);
        factoryBean.setMappings(ImmutableMap.of("SOURCE", "TYPE", "CLASS", "FIELD"));
    }

    @Test
    public void correctlyMapsEnumerations() throws Exception {
        final SimpleUnidirectionalConverter<RetentionPolicy, ElementType> converter = createConverter();

        assertThat(converter.convert(RetentionPolicy.SOURCE), is(ElementType.TYPE));
        assertThat(converter.convert(RetentionPolicy.CLASS), is(ElementType.FIELD));
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenSourceNotFound() throws Exception {
        final SimpleUnidirectionalConverter<RetentionPolicy, ElementType> converter = createConverter();

        converter.convert(RetentionPolicy.RUNTIME);
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenSourceIsNull() throws Exception {
        final SimpleUnidirectionalConverter<RetentionPolicy, ElementType> converter = createConverter();

        converter.convert(null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenSourceClassIsNotEnum() throws Exception {
        factoryBean.setSourceClass((Class) String.class);
        createConverter();
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenTargetClassIsNotEnum() throws Exception {
        factoryBean.setTargetClass((Class) String.class);
        createConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenMappingIsEmpty() throws Exception {
        factoryBean.setMappings(ImmutableMap.<String, String>of());
        createConverter();
    }

    @Test
    public void throwsExceptionWhenTargetEnumMappingHasInvalidName() throws Exception {
        factoryBean.setMappings(ImmutableMap.of("SOURCE", "TYPE", "SOURCE_DOES_NOT_EXIST", "FIELD"));
        try {
            createConverter();
            fail("Expected ConversionException");
        } catch (ConversionException e) {
            assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
            assertThat(e.getCause().getMessage(),
                    is("Cannot find SOURCE_DOES_NOT_EXIST in enum class java.lang.annotation.RetentionPolicy"));
        }
    }

    @Test
    public void throwsExceptionWhenSourceEnumMappingHasInvalidName() throws Exception {
        factoryBean.setMappings(ImmutableMap.of("SOURCE", "TYPE", "CLASS", "TARGET_DOES_NOT_EXIST"));
        try {
            createConverter();
            fail("Expected ConversionException");
        } catch (ConversionException e) {
            assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
            assertThat(e.getCause().getMessage(),
                    is("Cannot find TARGET_DOES_NOT_EXIST in enum class java.lang.annotation.ElementType"));
        }
    }

    private SimpleUnidirectionalConverter<RetentionPolicy, ElementType> createConverter() throws Exception {
            return factoryBean.getObject();
    }

}
