package org.bobba.tools.tests.common.utils;

import org.junit.Test;

import java.awt.Dimension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bobba.tools.tests.common.utils.ExpressionReplace.replaceExpressions;

public class ExpressionReplaceTest {

    @Test
    public void correctlyReplacesWhenNoExpressions() {
        assertThat(replaceExpressions("abc def", null)).isEqualTo("abc def");
    }

    @Test
    public void correctlyReplacesWhenExpressionInTheMiddle() {
        assertThat(replaceExpressions("abc ${1 + 2} def", null)).isEqualTo("abc 3 def");
    }

    @Test
    public void correctlyReplacesWhenExpressionsInFrontAndBack() {
        final Dimension dimension = new Dimension(33, 11);
        assertThat(replaceExpressions("${3 + 2} abc ${1 + 2} : ${width + height}", dimension))
                .isEqualTo("5 abc 3 : 44.0");
    }

    @Test
    public void correctlyReplacesWhenOnlyExpression() {
        assertThat(replaceExpressions("${3 + 2}", null)).isEqualTo("5");
    }

    @Test
    public void showsLongValue() {
        assertThat(replaceExpressions("${longValue()} being ${class}", 33L)).isEqualTo("33 being class java.lang.Long");
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionWhenNoProperty() {
        replaceExpressions("${doesNotExist}", 33L);
    }

    @Test
    public void replacesNullValueWithNothing() {
        assertThat(replaceExpressions("this is${cause}x", new Throwable())).isEqualTo("this isx");
    }

}
