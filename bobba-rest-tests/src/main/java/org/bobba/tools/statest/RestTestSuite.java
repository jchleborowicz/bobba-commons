package org.bobba.tools.statest;

import org.bobba.tools.statest.common.junit.RestSuiteJUnitRunner;
import org.bobba.tools.statest.common.junit.RestTestSuiteDefinition;
import org.junit.runner.RunWith;

@RunWith(RestSuiteJUnitRunner.class)
public class RestTestSuite {

    @RestSuiteJUnitRunner.DefinitionFactory
    public static RestTestSuiteDefinition createDefinition() {
        return RestTestSuiteDefinition.Builder.newInstance()
                .testStep(RestTestSuite.class)
                .build();
    }

}
