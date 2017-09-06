package org.bobba.tools.statest.common.junit;

import java.lang.annotation.Annotation;

public interface CustomParameterFactory<T extends Annotation> {

    Class<T> getSupportedAnnotationClass();

    Object getParameter(T annotation, Class<?> parameterClass, TestStateRepository testStateRepository);
}
