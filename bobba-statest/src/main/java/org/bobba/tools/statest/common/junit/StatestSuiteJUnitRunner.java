package org.bobba.tools.statest.common.junit;

import org.bobba.tools.statest.utils.StatestCommonUtils;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class StatestSuiteJUnitRunner extends ParentRunner<Runner> {

    private final List<Runner> runners;

    public StatestSuiteJUnitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        final StatestSuiteDefinition definition = createRestSuiteDefinition(testClass);
        runners = definition.createRunners(testClass);
    }

    private static StatestSuiteDefinition createRestSuiteDefinition(Class<?> testClass) {
        try {
            final Method method = getDefinitionFactoryMethod(testClass);
            return (StatestSuiteDefinition) method.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find createDefinition method in class " + testClass.getName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error when executing createDefinition method for class " + testClass.getName());
        }
    }

    private static Method getDefinitionFactoryMethod(Class<?> testClass) throws NoSuchMethodException {
        final Method method = findDefinitionFactoryAnnotatedMethod(testClass);

        if (method.getReturnType() != StatestSuiteDefinition.class) {
            throw new RuntimeException("createDefinition method in class " + testClass.getName()
                    + " should return StatestSuiteDefinition class");
        }
        return method;
    }

    private static Method findDefinitionFactoryAnnotatedMethod(Class<?> testClass) {
        final List<Method> methods = StatestCommonUtils.getMethodsAnnotatedWith(testClass,
                StatestSuiteJUnitRunner.DefinitionFactory.class);

        final int annotatedMethodCount = methods.size();
        if (annotatedMethodCount == 1) {
            return validateDefinitionFactoryMethod(methods.get(0));
        } else {
            throw new RuntimeException("Class " + testClass.getName() + " should contain one method annotated with "
                    + DefinitionFactory.class.getName() + " annotation. Currently there is "
                    + annotatedMethodCount + " annotated methods.");
        }
    }

    private static Method validateDefinitionFactoryMethod(Method method) {
        if (method.getParameterTypes().length != 0) {
            throw new RuntimeException("Method annotated with " + DefinitionFactory.class.getName()
                    + " should not have any parameters. Current method declaration: " + method);
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Method annotated with " + DefinitionFactory.class.getName()
                    + " should be static. Current method declaration: " + method);
        }
        return method;
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner child, final RunNotifier notifier) {
        notifier.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                notifier.pleaseStop();
            }
        });
        child.run(notifier);
    }

    /**
     * Interface marking definition factory method in rest test suite.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface DefinitionFactory {
    }
}
