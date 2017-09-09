package org.bobba.tools.statest.common.junit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.bobba.tools.statest.utils.CommonUtils;
import org.bobba.tools.statest.common.AnnotatedParameterFactories;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatestJUnitClassRunner extends BlockJUnit4ClassRunner {

    private static final Comparator<FrameworkMethod> TEST_ORDER_COMPARATOR = new TestOrderComparator();
    private static final TestStateRepository TEST_STATE_REPOSITORY;

    static {
        final File baseDir = new File(System.getProperty("java.io.tmpdir"));
        final File repositoryDir = new File(baseDir, "rest-tests");

        TEST_STATE_REPOSITORY = new FileBasedTestStateRepository(repositoryDir);
    }

    public StatestJUnitClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        final List<FrameworkMethod> result = getTestMethods();

        result.sort(TEST_ORDER_COMPARATOR);

        return ImmutableList.copyOf(result);
    }

    private List<FrameworkMethod> getTestMethods() {
        final List<FrameworkMethod> result =
                new ArrayList<>(getTestClass().getAnnotatedMethods(Statest.class));
        final List<FrameworkMethod> testAnnotatedMethods = getTestClass().getAnnotatedMethods(Test.class);
        for (FrameworkMethod testAnnotatedMethod : testAnnotatedMethods) {
            if (!result.contains(testAnnotatedMethod)) {
                result.add(testAnnotatedMethod);
            }
        }

        return result;
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        final List<FrameworkMethod> testMethods = getTestMethods();
        final ImmutableListMultimap<String, FrameworkMethod> methodsByName =
                Multimaps.index(testMethods, FrameworkMethod::getName);
        final StringBuilder errorMessage = new StringBuilder();
        for (Collection<FrameworkMethod> methods : methodsByName.asMap().values()) {
            if (methods.size() > 1) {
                errorMessage.append(" - ")
                        .append(CommonUtils.createCodePointer(methods.iterator().next().getMethod()))
                        .append("\n");
            }
        }
        if (errorMessage.length() > 0) {
            errors.add(new RuntimeException("Test method name duplicated:\n" + errorMessage));
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return new InvokeStatestMethodStatement(method, test, TEST_STATE_REPOSITORY,
                determineCustomParameterFactories());
    }

    private ImmutableMap<Class<? extends Annotation>, CustomParameterFactory<?>> determineCustomParameterFactories() {
        final Map<Class<? extends Annotation>, CustomParameterFactory<?>> result = Maps.newHashMap();

        addCustomParameterHandler(result, new TestStateCustomParameterFactory());
        addCustomParameterFactories(result, getTestClass().getJavaClass());

        return ImmutableMap.copyOf(result);
    }

    private void addCustomParameterFactories(Map<Class<? extends Annotation>, CustomParameterFactory<?>> result,
                                             Class<?> testClass) {
        if (testClass == Object.class) {
            return;
        }
        final AnnotatedParameterFactories factories = testClass.getAnnotation(AnnotatedParameterFactories.class);
        if (factories != null) {
            for (Class<? extends CustomParameterFactory<?>> parameterFactoryClass : factories.value()) {
                try {
                    final CustomParameterFactory<?> customParameterFactory = parameterFactoryClass.newInstance();
                    addCustomParameterHandler(result, customParameterFactory);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        addCustomParameterFactories(result, testClass.getSuperclass());
    }

    private void addCustomParameterHandler(Map<Class<? extends Annotation>, CustomParameterFactory<?>> result,
                                           CustomParameterFactory<?> customParameterFactory) {
        final Class<? extends Annotation> supportedAnnotationClass =
                customParameterFactory.getSupportedAnnotationClass();
        if (result.containsKey(supportedAnnotationClass)) {
            throw new RuntimeException(
                    "Custom parameter factory defined twice for annotation: " + supportedAnnotationClass.getName());
        }
        result.put(supportedAnnotationClass, customParameterFactory);
    }

    @Override
    protected void runChild(FrameworkMethod method, final RunNotifier notifier) {
        notifier.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                notifier.pleaseStop();
            }
        });

        super.runChild(method, notifier);
    }

    private static class TestOrderComparator implements Comparator<FrameworkMethod> {
        @Override
        public int compare(FrameworkMethod o1, FrameworkMethod o2) {
            final Statest o1Annotation = CommonUtils.getMethodAnnotation(o1.getMethod(), Statest.class);
            final Statest o2Annotation = CommonUtils.getMethodAnnotation(o2.getMethod(), Statest.class);

            if (o1Annotation == null) {
                if (o2Annotation == null) {
                    return 0;
                } else {
                    return 1;
                }
            }

            if (o2Annotation == null) {
                return -1;
            }

            return o1Annotation.order() - o2Annotation.order();
        }
    }
}
