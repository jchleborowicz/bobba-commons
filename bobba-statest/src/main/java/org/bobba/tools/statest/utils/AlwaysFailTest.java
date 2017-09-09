package org.bobba.tools.statest.utils;

import org.bobba.tools.statest.common.junit.Statest;
import org.bobba.tools.statest.common.junit.StatestRunner;
import org.junit.runner.RunWith;

@RunWith(StatestRunner.class)
public class AlwaysFailTest {

    @Statest
    public void testIt() {
        throw new RuntimeException("Mysterious message");
    }

}
