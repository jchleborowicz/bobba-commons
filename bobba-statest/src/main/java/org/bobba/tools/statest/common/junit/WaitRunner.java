package org.bobba.tools.statest.common.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WaitRunner extends Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitRunner.class);

    private final long delayInSeconds;
    private final int testUniqueId;

    public WaitRunner(long delayInSeconds, int testUniqueId) {
        this.delayInSeconds = delayInSeconds;
        this.testUniqueId = testUniqueId;
    }

    @Override
    public Description getDescription() {
        return Description.createTestDescription(getClass().getName(), "Waiting for " + delayInSeconds + " seconds",
                "waiting-" + testUniqueId);
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            notifier.fireTestStarted(getDescription());
            LOGGER.info("Waiting for " + delayInSeconds + " seconds");
            Thread.sleep(TimeUnit.SECONDS.toMillis(delayInSeconds));
            LOGGER.info("Woken up from waiting");
            notifier.fireTestFinished(getDescription());
        } catch (InterruptedException e) {
            LOGGER.info("Waiting interrupted! Stopping test suite");
            notifier.fireTestFailure(new Failure(Description.createSuiteDescription("Waiting interrupted"), null));
            notifier.pleaseStop();
        }
    }
}
