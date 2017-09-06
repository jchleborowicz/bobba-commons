package org.bobba.tools.tests.common.utils;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableReplace {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    public static String replaceVariables(String text, Map<String, String> variables) {
        return replaceVariables(text, variables::get);
    }

    /**
     * Replaces all variables in given text.
     * <p>
     * Example input stream "You can't ${var1} get what you want" has one variable named "var1".
     * If variableReplaceFunction returns "always" for variable named "var1", then result would be
     * "You can't always get what you want".
     *
     * @param text                    input text.
     * @param variableReplaceFunction a function which accepts variable name and returns a text which should replace
     *                                a variable.
     * @return input text with variable placeholders replaced with values provided by
     * variableReplaceFunction.
     */
    public static String replaceVariables(String text, Function<String, String> variableReplaceFunction) {
        final Matcher matcher = VARIABLE_PATTERN.matcher(text);
        final StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            final String replacement = variableReplaceFunction.apply(matcher.group(1));
            matcher.appendReplacement(result, replacement);
            result.append("");
        }

        matcher.appendTail(result);

        return result.toString();
    }

}
