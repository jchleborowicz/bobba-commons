package org.bobba.tools.statest.common.junit;

import com.google.common.collect.ImmutableList;
import org.junit.runner.Runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatestSuiteDefinition {

    private final List<TestRunnerCreator> testRunnerCreators;
    private final AtomicInteger idGenerator = new AtomicInteger(0);


    public StatestSuiteDefinition(List<TestRunnerCreator> testRunnerCreators) {
        this.testRunnerCreators = ImmutableList.copyOf(testRunnerCreators);
    }

    public List<Runner> createRunners(final Class<?> suiteTestClass) {
        final List<Runner> result = new ArrayList<>();
        for (TestRunnerCreator testRunnerCreator : testRunnerCreators) {
            result.add(testRunnerCreator.createRunner(suiteTestClass, idGenerator.incrementAndGet()));
        }
        return result;
    }

    public interface TestRunnerCreator {
        Runner createRunner(Class<?> suiteTestClass, int testUniqueId);
    }

    public static final class Builder {

        private final List<TestRunnerCreator> testRunnerCreators = new ArrayList<>();

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder testStep(Class<?> testClass) {
            testRunnerCreators.add(new ClassBasedTestRunner(testClass));
            return this;
        }

        public Builder delay(int delayInSeconds) {
            testRunnerCreators.add(new WaitRunnerCreator(delayInSeconds));
            return this;
        }

        public StatestSuiteDefinition build() {
            return new StatestSuiteDefinition(testRunnerCreators);
        }

    }

    private static final class ClassBasedTestRunner implements TestRunnerCreator {

        private final Class<?> testClass;

        public ClassBasedTestRunner(Class<?> testClass) {
            this.testClass = testClass;
        }

        public Runner createRunner(Class<?> suiteTestClass, int testUniqueId) {
            try {
                return new StatestRunner(testClass);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    private static final class WaitRunnerCreator implements TestRunnerCreator {

        private final long delayInSeconds;

        public WaitRunnerCreator(long delayInSeconds) {
            this.delayInSeconds = delayInSeconds;
        }

        public Runner createRunner(Class<?> suiteTestClass, int testUniqueId) {
            return new WaitRunner(delayInSeconds, testUniqueId);
        }
    }

}
