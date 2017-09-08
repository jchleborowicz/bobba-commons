package org.bobba.tools.statest.utils;

import org.bobba.tools.statest.common.junit.RestTest;
import org.bobba.tools.statest.common.junit.RestTestJUnitClassRunner;
import org.junit.runner.RunWith;

@RunWith(RestTestJUnitClassRunner.class)
public class AlwaysFailRestTest {

    @RestTest
    public void testIt() {
        throw new RuntimeException("Mysterious message");
    }

}
