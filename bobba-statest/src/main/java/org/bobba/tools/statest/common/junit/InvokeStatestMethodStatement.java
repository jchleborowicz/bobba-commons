package org.bobba.tools.statest.common.junit;

import com.google.common.collect.ImmutableMap;
import org.bobba.tools.statest.utils.StatestCommonUtils;
import org.bobba.tools.statest.common.StatestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.Validate.notNull;

public class InvokeStatestMethodStatement extends Statement {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeStatestMethodStatement.class);

    private final FrameworkMethod testMethod;
    private final Object target;
    private final TestStateRepository testStateRepository;
    private final ImmutableMap<Class<? extends Annotation>, CustomParameterFactory<?>> customParameterFactories;

    public InvokeStatestMethodStatement(FrameworkMethod testMethod, Object target,
                                        TestStateRepository testStateRepository,
                                        ImmutableMap<Class<? extends Annotation>, CustomParameterFactory<?>>
                                                 customParameterFactories) {
        this.testMethod = testMethod;
        this.target = target;
        this.testStateRepository = testStateRepository;
        this.customParameterFactories = customParameterFactories;
    }

    @Override
    public void evaluate() throws Throwable {
        LOGGER.info("Invoking test method " + StatestCommonUtils.createCodePointer(testMethod.getMethod()));
        final Object[] params = calculateParameters();

        final Object result = testMethod.invokeExplosively(target, params);

        storeResult(result, testMethod.getMethod());
    }

    private Object[] calculateParameters() {
        final Method method = testMethod.getMethod();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        if (parameterTypes.length != parameterAnnotations.length) {
            throw new RuntimeException("Parameter types size not equal parameter annotations size for method "
                    + StatestCommonUtils.createCodePointer(method));
        }

        final Object[] result = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                result[i] = loadContextObject(parameterTypes[i], parameterAnnotations[i]);
            } catch (RuntimeException e) {
                throw new RuntimeException("Exception when loading parameter for method "
                        + StatestCommonUtils.createCodePointer(method), e);
            }
        }
        return result;
    }

    private <T> T loadContextObject(Class<T> parameterType, Annotation[] parameterAnnotation) {
        final Annotation testAnnotation = findTestAnnotation(parameterAnnotation);

        if (testAnnotation != null) {
            final CustomParameterFactory customParameterFactory =
                    customParameterFactories.get(testAnnotation.annotationType());
            //noinspection unchecked
            final Object result =
                    customParameterFactory.getParameter(testAnnotation, parameterType, testStateRepository);
            if (result != null && !parameterType.isInstance(result)) {
                throw new RuntimeException(
                        "Parameter returned from parameter factory is not of expected type. Expected type: "
                                + parameterType.getName() + ", returned object type: " + result.getClass().getName());
            }
            //noinspection unchecked
            return (T) result;
        }

        final String parameterId = StatestUtils.defaultObjectId(parameterType);

        final T result = testStateRepository.load(parameterId, parameterType);
        LOGGER.info("Loaded test state object. Id: " + parameterId + ", value: " + result);
        return result;
    }

    private Annotation findTestAnnotation(Annotation[] parameterAnnotation) {
        Annotation result = null;
        for (Annotation annotation : parameterAnnotation) {
            if (customParameterFactories.containsKey(annotation.annotationType())) {
                if (result != null) {
                    throw new RuntimeException("Found two parameter annotations: " + result.annotationType().getName()
                            + " and " + annotation.annotationType().getName());
                }
                result = annotation;
            }
        }
        return result;
    }

    private void storeResult(Object object, Method method) {
        final Statest statestAnnotation = StatestCommonUtils.getMethodAnnotation(method, Statest.class);
        final String annotationObjectId =
                statestAnnotation == null || StringUtils.isEmpty(statestAnnotation.storeResultIn()) ? null :
                        statestAnnotation.storeResultIn().trim();
        final Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == Void.class || returnType == Void.TYPE) {
            if (annotationObjectId != null) {
                throw new RuntimeException(
                        "Marked test result storage (@Statest.storeResultIn) on method that returns void: " +
                                StatestCommonUtils.createCodePointer(method));
            }
            return;
        }

        if (returnType == GenericStatestResult.class) {
            handleGenericResult((GenericStatestResult) object, annotationObjectId);
        } else {
            handleSimpleResult(object, annotationObjectId, returnType);
        }
    }

    private void handleGenericResult(GenericStatestResult result, String annotationObjectId) {
        if (annotationObjectId != null) {
            throw new RuntimeException("Explicitly specifying object id for GenericStatestResult is not supported");
        }
        if (result == null) {
            return;
        }
        for (GenericStatestResult.Entry entry : result) {
            final Object value = entry.getValue();
            final String objectId = determineObjectId(entry, value);
            testStateRepository.store(objectId, value);
        }
    }

    private void handleSimpleResult(Object object, String annotationObjectId, Class<?> returnType) {
        final String objectId = determineObjectId(annotationObjectId, returnType);
        testStateRepository.store(objectId, object);
    }

    private String determineObjectId(GenericStatestResult.Entry entry, Object value) {
        return StringUtils.isEmpty(entry.getObjectId()) ? defaultObjectId(value) : entry.getObjectId();
    }

    private String determineObjectId(String proposedObjectId, Class<?> returnType) {
        return proposedObjectId == null ? StatestUtils.defaultObjectId(returnType) : proposedObjectId;
    }

    private String defaultObjectId(Object value) {
        notNull(value, "Cannot determine default object id for empty object");
        return StatestUtils.defaultObjectId(value.getClass());
    }

}
