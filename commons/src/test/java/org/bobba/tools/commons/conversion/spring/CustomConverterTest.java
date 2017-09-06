package org.bobba.tools.commons.conversion.spring;

import org.bobba.tools.commons.conversion.ConversionException;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("CustomConverterTest.spring.xml")
public class CustomConverterTest {

    @Autowired
    @Qualifier("errorCodeConverter")
    private SimpleUnidirectionalConverter<RetentionPolicy, ElementType> converter;

    @Test
    public void correctlyMapsEnumerations() throws Exception {
        assertThat(converter.convert(RetentionPolicy.SOURCE), is(ElementType.TYPE));
        assertThat(converter.convert(RetentionPolicy.CLASS), is(ElementType.FIELD));
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenSouceNotFound() throws Exception {
        converter.convert(RetentionPolicy.RUNTIME);
    }

    @Test(expected = ConversionException.class)
    public void throwsExceptionWhenSouceIsNull() throws Exception {
        converter.convert(null);
    }
}
