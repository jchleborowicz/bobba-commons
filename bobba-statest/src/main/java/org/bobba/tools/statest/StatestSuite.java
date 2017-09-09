package org.bobba.tools.statest;

import org.bobba.tools.statest.common.junit.StatestSuiteDefinition;
import org.bobba.tools.statest.common.junit.StatestSuiteJUnitRunner;
import org.junit.runner.RunWith;

@RunWith(StatestSuiteJUnitRunner.class)
public class StatestSuite {

    @StatestSuiteJUnitRunner.DefinitionFactory
    public static StatestSuiteDefinition createDefinition() {
        return StatestSuiteDefinition.Builder.newInstance()
                .testStep(StatestSuite.class)
                .build();
    }

}
