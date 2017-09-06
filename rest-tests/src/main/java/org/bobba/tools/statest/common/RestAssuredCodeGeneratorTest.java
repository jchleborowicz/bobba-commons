package org.bobba.tools.statest.common;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RestAssuredCodeGeneratorTest {

    @Test
    public void createsCorrectMatcher() {
        final Matcher<?> matcher = RestAssuredCodeGenerator.assertionGeneratingMatcher();

        try {
            matcher.matches("{\"abc\": \"def\", \"arr\" : [ \"a1\", 9 ] }");
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Stopped by assertion generator"));
        }
    }

}