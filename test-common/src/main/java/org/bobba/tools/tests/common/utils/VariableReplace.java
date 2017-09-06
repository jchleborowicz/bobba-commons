package org.bobba.tools.tests.common.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableReplace {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    public static String replaceVariables(String text, Map<String, String> variables) {
        return replaceVariables(text, variables::get);
    }

    public static String replaceVariables(String text, VariableReplaceCallback variableReplaceCallback) {
        final Matcher matcher = VARIABLE_PATTERN.matcher(text);
        final StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            final String replacement = variableReplaceCallback.replace(matcher.group(1));
            matcher.appendReplacement(result, replacement);
            result.append("");
        }

        matcher.appendTail(result);

        return result.toString();
    }

    public interface VariableReplaceCallback {
        String replace(String variableText);
    }
}
