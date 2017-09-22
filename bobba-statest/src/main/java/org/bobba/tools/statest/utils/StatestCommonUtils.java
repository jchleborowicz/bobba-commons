package org.bobba.tools.statest.utils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class StatestCommonUtils {

    private StatestCommonUtils() {
    }

    public static void checkObjectType(Object object, Class<?> expectedType) {
        if (object != null && !expectedType.isInstance(object)) {
            throw new IllegalArgumentException(
                    "Object is not of expected type. Expected: " + expectedType + ". Actual: "
                            + object.getClass());
        }
    }

    public static List<Method> getMethodsAnnotatedWith(Class<?> aClass, Class<?> annotationClass) {
        final List<Method> result = new ArrayList<>();
        final Method[] declaredMethods = aClass.getMethods();
        for (Method declaredMethod : declaredMethods) {
            if (containsAnnotation(declaredMethod, annotationClass)) {
                result.add(declaredMethod);
            }
        }
        return result;
    }

    public static boolean containsAnnotation(Method declaredMethod, Class<?> annotationClass) {
        return getMethodAnnotation(declaredMethod, annotationClass) != null;
    }

    public static <T> T getMethodAnnotation(Method declaredMethod, Class<T> annotationClass) {
        final Annotation[] declaredAnnotations = declaredMethod.getDeclaredAnnotations();
        return getAnnotationByType(declaredAnnotations, annotationClass);
    }

    public static <T> T getClassAnnotation(Class<?> aClass, Class<T> annotationClass) {
        return getAnnotationByType(aClass.getDeclaredAnnotations(), annotationClass);
    }

    public static <T> T getAnnotationByType(Annotation[] declaredAnnotations, Class<T> annotationClass) {
        if (declaredAnnotations == null) {
            return null;
        }
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.annotationType() == annotationClass) {
                //noinspection unchecked
                return (T) declaredAnnotation;
            }
        }
        return null;
    }

    public static String createCodePointer(String className, String methodName, int lineNumber) {
        final String shortClassName = ClassUtils.getShortClassName(className);
        return className + "." + methodName + "(" + shortClassName + ".java:" + lineNumber + ")";
    }

    public static String createCodePointer(StackTraceElement stackTraceElement) {
        return createCodePointer(stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
                stackTraceElement.getLineNumber());
    }

    public static String createCodePointer(Method method) {
        final int lineNumber = getMethodLineNumber(method);
        return createCodePointer(method.getDeclaringClass().getName(), method.getName(), lineNumber);
    }

    private static int getMethodLineNumber(Method method) {
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass cc = pool.get(method.getDeclaringClass().getCanonicalName());
            CtMethod javassistMethod = cc.getDeclaredMethod(method.getName());
            return javassistMethod.getMethodInfo().getLineNumber(0);
        } catch (NotFoundException e) {
            //todo jch deal with exception thrown
            return 0;
        }
    }

    /**
     * Creates code pointer as string.
     *
     * @param callStackRelativePosition determines, line for which code pointer will be generated:
     *                                  <ul>
     *                                  <li>0 means current call line.</li>
     *                                  <li>-1 means line in which current method is called.</li>
     *                                  <li>-2 means line in which method calling current method is called.</li>
     *                                  <li>-3 means line in which method calling method calling current method is
     *                                  called.</li>
     *                                  <li>and so on...</li>
     *                                  </ul>
     * @throws IllegalArgumentException when callStackRelativePosition is positive.
     */
    public static String createCodePointer(int callStackRelativePosition) {
        if (callStackRelativePosition > 0) {
            throw new RuntimeException("Not accepting positive stack relative positions");
        }
        final StackTraceElement stackTraceElement =
                Thread.currentThread().getStackTrace()[-callStackRelativePosition + 2];
        return createCodePointer(stackTraceElement);
    }

}
