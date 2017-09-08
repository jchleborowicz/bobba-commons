package org.bobba.tools.statest.common;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class RestAssuredCodeGeneratorTest {

    @Test
    public void createsCorrectMatcher() {
        final Matcher<?> matcher = RestAssuredCodeGenerator.assertionGeneratingMatcher();

        try {
            matcher.matches("{\"abc\": \"def\", \"arr\" : [ \"a1\", 9 ] }");
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Stopped by assertion generator");
        }
    }

}
