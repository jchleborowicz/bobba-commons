package org.bobba.tools.statest.common;

import com.google.common.collect.ImmutableMap;
import org.bobba.tools.statest.common.junit.CustomParameterFactory;
import org.bobba.tools.statest.common.junit.TestStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class TestGenerateParameterFactory implements CustomParameterFactory<TestGenerate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenerateParameterFactory.class);

    private static final Map<Class<?>, ParameterValueGenerator<?>> PARAMETER_GENERATORS =
            ImmutableMap.of(
                    Integer.class, new IntegerParameterGenerator()
            );

    @Override
    public Class<TestGenerate> getSupportedAnnotationClass() {
        return TestGenerate.class;
    }

    @Override
    public Object getParameter(TestGenerate annotation, Class<?> parameterClass,
                               TestStateRepository testStateRepository) {
        final ParameterValueGenerator<?> parameterValueGenerator = getParameterGenerator(parameterClass);

        final Object result = parameterValueGenerator.generate();

        LOGGER.info("Generated parameter value: " + result);

        return result;
    }

    private ParameterValueGenerator<?> getParameterGenerator(Class<?> parameterClass) {
        final ParameterValueGenerator<?> result = PARAMETER_GENERATORS.get(parameterClass);

        if (result == null) {
            throw new RuntimeException(
                    "Class " + parameterClass.getName() + " is not supported by @TestGenerate annotation.");
        }

        return result;
    }

    private interface ParameterValueGenerator<T> {
        T generate();
    }

    private static class IntegerParameterGenerator implements ParameterValueGenerator<Integer> {
        private static Random random = new Random();

        @Override
        public Integer generate() {
            return random.nextInt();
        }
    }

}
