package org.bobba.tools.commons.conversion;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.EnumUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public final class CommonUtils {

    private CommonUtils() {
    }

    public static void checkRequiredClassType(Class<?> aClass, Class<?> expectedType) {
        notNull(aClass);
        if (!expectedType.isAssignableFrom(aClass)) {
            throw new IllegalArgumentException(
                    "Class " + aClass.getName() + " should descend from class " + expectedType);
        }
    }

    public static void checkRequiredObjectType(Object object, Class<?> expectedType) {
        notNull(object);
        checkObjectType(object, expectedType);
    }

    public static void checkObjectType(Object object, Class<?> expectedType) {
        if (object != null && !expectedType.isInstance(object)) {
            throw new IllegalArgumentException(
                    "Object is not of expected type. Expected: " + expectedType + ". Actual: "
                            + object.getClass());
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        final T result = EnumUtils.getEnum(enumClass, name);
        if (result == null) {
            throw new IllegalArgumentException("Cannot find " + name + " in enum class " + enumClass.getName());
        }
        return result;
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

    /**
     * Creates code pointer to calling line.
     */
    public static String createCodePointer() {
        return createCodePointer(-1);
    }

}

