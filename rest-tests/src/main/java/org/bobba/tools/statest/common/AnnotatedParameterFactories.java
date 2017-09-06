package org.bobba.tools.statest.common;

import org.bobba.tools.statest.common.junit.CustomParameterFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotatedParameterFactories {

    Class<? extends CustomParameterFactory<?>>[] value();

}
