package org.bobba.tools.statest.utils;

import org.bobba.tools.statest.common.junit.Statest;
import org.bobba.tools.statest.common.junit.StatestJUnitClassRunner;
import org.junit.runner.RunWith;

@RunWith(StatestJUnitClassRunner.class)
public class AlwaysFailTest {

    @Statest
    public void testIt() {
        throw new RuntimeException("Mysterious message");
    }

}
