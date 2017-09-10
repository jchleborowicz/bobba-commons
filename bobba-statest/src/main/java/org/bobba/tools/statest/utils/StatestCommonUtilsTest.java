package org.bobba.tools.statest.utils;

import org.junit.Test;

public class StatestCommonUtilsTest {

    @Test
    public void checkObjectTypePassesWhenTypeIsAsExpected() {
        StatestCommonUtils.checkObjectType(1, Integer.class);
    }

    @Test
    public void checkObjectTypePassesWhenNullSourceObject() {
        StatestCommonUtils.checkObjectType(null, Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkObjectTypeThrowsExceptionWhenUnexpectedType() {
        StatestCommonUtils.checkObjectType(1, Long.class);
    }

}
