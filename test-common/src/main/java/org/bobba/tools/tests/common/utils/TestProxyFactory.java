package org.bobba.tools.tests.common.utils;

import com.google.common.base.Joiner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class TestProxyFactory {

    private TestProxyFactory() {
    }

    public static <T> T newExceptionThrowingProxy(Class<T> aClass) {
        final InvocationHandler infocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                throw new RuntimeException("Trying to execute method on exception-throwing proxy: " + method
                        + ", arguments: " + join(args));
            }

            private String join(Object[] args) {
                return args == null ? "null" : Joiner.on(",").join(args);
            }
        };

        //noinspection unchecked
        return (T) Proxy.newProxyInstance(TestProxyFactory.class.getClassLoader(), new Class[]{aClass},
                infocationHandler);
    }

}
