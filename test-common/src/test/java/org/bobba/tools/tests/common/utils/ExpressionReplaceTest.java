package org.bobba.tools.tests.common.utils;

import org.junit.Test;

import java.awt.Dimension;

import static org.bobba.tools.tests.common.utils.ExpressionReplace.replaceExpressions;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ExpressionReplaceTest {

    @Test
    public void correctlyReplacesWhenNoExpressions() {
        assertThat(replaceExpressions("abc def", null), equalTo("abc def"));
    }

    @Test
    public void correctlyReplacesWhenExpressionInTheMiddle() {
        assertThat(replaceExpressions("abc ${1 + 2} def", null), equalTo("abc 3 def"));
    }

    @Test
    public void correctlyReplacesWhenExpressionsInFrontAndBack() {
        final Dimension dimension = new Dimension(33, 11);
        assertThat(replaceExpressions("${3 + 2} abc ${1 + 2} : ${width + height}", dimension),
                equalTo("5 abc 3 : 44.0"));
    }

    @Test
    public void correctlyReplacesWhenOnlyExpression() {
        assertThat(replaceExpressions("${3 + 2}", null), equalTo("5"));
    }

    @Test
    public void showsLongValue() {
        assertThat(replaceExpressions("${longValue()} being ${class}", 33L), equalTo("33 being class java.lang.Long"));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionWhenNoProperty() {
        replaceExpressions("${doesNotExist}", 33L);
    }

    @Test
    public void replacesNullValueWithNothing() {
        assertThat(replaceExpressions("this is${cause}x", new Throwable()), equalTo("this isx"));
    }

}
