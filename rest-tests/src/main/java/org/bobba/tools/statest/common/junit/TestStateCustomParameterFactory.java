package org.bobba.tools.statest.common.junit;

import org.bobba.tools.statest.common.RestTestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestStateCustomParameterFactory implements CustomParameterFactory<TestState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStateCustomParameterFactory.class);

    @Override
    public Class<TestState> getSupportedAnnotationClass() {
        return TestState.class;
    }

    @Override
    public Object getParameter(TestState annotation, Class<?> parameterClass,
                               TestStateRepository testStateRepository) {
        final String parameterId = determineObjectId(parameterClass, annotation);
        try {
            final Object result = testStateRepository.load(parameterId, parameterClass);
            LOGGER.info("Loaded test state object. Id: " + parameterId + ", value: " + result);
            return result;
        } catch (TestStateObjectDoesNotExistException e) {
            if (annotation.optional()) {
                return null;
            }
            throw e;
        }
    }

    private String determineObjectId(Class<?> parameterType, TestState annotation) {
        return StringUtils.isEmpty(annotation.objectId())
                ? RestTestUtils.defaultObjectId(parameterType) : annotation.objectId();
    }

}
