package org.bobba.tools.tests.common.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class VariableReplaceTest {

    public static final Map<String, String> VARIABLE_MAP = new HashMap<String, String>() {{
        put("var1", "world");
        put("var2", "vampire");
    }};

    @Test
    public void correctlyReplacesVariablesWithCallback() {
        final String result = VariableReplace.replaceVariables("The ${var1} is a ${var2}.", VARIABLE_MAP::get);

        assertThat(result).isEqualTo("The world is a vampire.");
    }

    @Test
    public void correctlyReplacesVariablesWithMap() {
        final String result = VariableReplace.replaceVariables("The ${var1} is a ${var2}.", VARIABLE_MAP);

        assertThat(result).isEqualTo("The world is a vampire.");
    }

}
