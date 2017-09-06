package org.bobba.tools.commons;

import org.bobba.tools.commons.utils.CommonUtils;
import org.junit.Test;

public class CommonUtilsTest {

    @Test
    public void checkObjectTypePassesWhenTypeIsAsExpected() {
        CommonUtils.checkObjectType(1, Integer.class);
    }

    @Test
    public void checkObjectTypePassesWhenNullSourceObject() {
        CommonUtils.checkObjectType(null, Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkObjectTypeThrowsExceptionWhenUnexpectedType() {
        CommonUtils.checkObjectType(1, Long.class);
    }

}
