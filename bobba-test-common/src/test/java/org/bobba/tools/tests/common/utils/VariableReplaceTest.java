package org.bobba.tools.tests.common.utils;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VariableReplaceTest {

    public static final ImmutableMap<String, String> VARIABLE_MAP = ImmutableMap.<String, String>builder()
            .put("var1", "world")
            .put("var2", "vampire")
            .build();

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
