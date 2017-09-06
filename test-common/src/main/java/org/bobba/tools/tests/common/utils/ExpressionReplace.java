package org.bobba.tools.tests.common.utils;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

public class ExpressionReplace {

    public static String replaceExpressions(String text, final Object contextObject) {
        return VariableReplace.replaceVariables(text, new VariableReplace.VariableReplaceCallback() {
            public String replace(String variableText) {
                return evaluate(variableText, contextObject);
            }
        });
    }

    private static String evaluate(String expression, Object contextObject) {
        try {
            final Object expr = Ognl.parseExpression(expression);
            final OgnlContext context = new OgnlContext();
            final Object value = Ognl.getValue(expr, context, contextObject);
            return value == null ? "" : value.toString();
        } catch (OgnlException e) {
            throw new RuntimeException("Exception when evaluating expression: " + expression, e);
        }
    }

}
