package org.bobba.tools.statest.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.bobba.tools.statest.utils.CommonUtils.getClassAnnotation;

public class StatestRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatestRunner.class);

    public static final String OPTION_LIST_TESTS = "l";
    public static final String OPTION_PERFORM_TESTS = "t";
    public static final String OPTION_ENV_CONFIG = "e";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder(OPTION_LIST_TESTS)
                .desc("Lists test classes.")
                .build());
        options.addOption(Option.builder(OPTION_PERFORM_TESTS)
                .desc("Runs sequentially tests specified as a parameter. Individual test should be separated by space.")
                .optionalArg(true)
                .hasArgs()
                .build());
        options.addOption(Option.builder(OPTION_ENV_CONFIG)
                .desc("envConfig. Obligatory.")
                .hasArg()
                .build());

        final CommandLineParser commandLineParser = new DefaultParser();

        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);


            if (commandLine.hasOption(OPTION_LIST_TESTS)) {
                setEnvConfigOverride(commandLine, false);
                listTests();
            } else if (commandLine.hasOption(OPTION_PERFORM_TESTS)) {
                setEnvConfigOverride(commandLine, true);
                executeTests(commandLine.getOptionValues(OPTION_PERFORM_TESTS));
            } else {
                throw new ParseException("Please provide program argument");
            }
        } catch (ParseException exception) {
            System.out.println("Incorrect arguments: " + exception.getMessage());
            printHelp(options);
            System.exit(-1);
        }
    }

    private static void setEnvConfigOverride(CommandLine commandLine, boolean obligatory) {
        final String envConfigOverride = commandLine.getOptionValue(OPTION_ENV_CONFIG);
        if (StringUtils.isEmpty(envConfigOverride)) {
            if (obligatory) {
                System.out.println("Please specify envConfig by using -" + OPTION_ENV_CONFIG + " option");
                System.exit(-1);
            }
        } else {
            System.setProperty("envConfigOverride", envConfigOverride);
        }
    }

    private static void executeTests(String[] args) throws ParseException {
        if (isEmpty(args)) {
            throw new ParseException("Please provide arguments for -" + OPTION_PERFORM_TESTS + " switch");
        }

        final List<Class> testsToExecute = getTestsClasses(args);

        for (Class aClass : testsToExecute) {
            LOGGER.info("Executing test: " + aClass.getName());
            final Result runResult = new JUnitCore().run(aClass);
            if (!runResult.wasSuccessful()) {
                LOGGER.error("Error result for test " + aClass.getName());
                for (Failure failure : runResult.getFailures()) {
                    LOGGER.error(failure.toString());
                }
                System.exit(-1);
            }
        }

        System.out.println("Executing tests: " + Joiner.on(", ").join(args));
    }

    private static List<Class> getTestsClasses(String[] tests) {
        final ArrayList<Class> result = new ArrayList<>();
        Map<String, Class<?>> testsBySimpleName = null;
        for (String test : tests) {
            final Class<?> testClass;
            if (test.contains(".")) {
                try {
                    testClass = Class.forName(test);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (testsBySimpleName == null) {
                    testsBySimpleName = getTestsBySimpleName();
                }
                testClass = testsBySimpleName.get(test);
                if (testClass == null) {
                    throw new RuntimeException("Cannot find test class for: " + test);
                }
            }
            result.add(testClass);
        }
        return result;
    }

    private static Map<String, Class<?>> getTestsBySimpleName() {
        final Iterable<Class<?>> allTestClasses = getAllTestClasses();
        final Set<String> repeatingSimpleNames = Sets.newHashSet();

        final Map<String, Class<?>> result = Maps.newHashMap();

        for (Class<?> testClass : allTestClasses) {
            final String simpleName = testClass.getSimpleName();
            if (!repeatingSimpleNames.contains(simpleName)) {
                if (result.containsKey(simpleName)) {
                    result.remove(simpleName);
                    repeatingSimpleNames.add(simpleName);
                } else {
                    result.put(simpleName, testClass);
                }
            }
        }
        return result;
    }

    private static void printHelp(Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pd-test.sh", options);
    }

    public static void listTests() {
        final Iterable<Class<?>> filtered = getAllTestClasses();

        int max = 0;
        for (Class<?> aClass : filtered) {
            final int length = aClass.getSimpleName().length();
            if (max < length) {
                max = length;
            }
        }

        max = Math.min(30, max);

        System.out.println("Available test classes:");
        for (Class<?> aClass : filtered) {
            System.out.println(StringUtils.rightPad(aClass.getSimpleName(), max) + " - " + aClass.getName());
        }
    }

    private static Iterable<Class<?>> getAllTestClasses() {
        final ArrayListConsumer<Class<?>> consumer = new ArrayListConsumer<>();

        iterateClassesFromSameJar(StatestRunner.class, consumer);

        final List<Class<?>> content = consumer.getContent();

        sortBySimpleClassName(content);

        final Class<?> baseStatestClass;
        try {
            //this is obtained from reflection by purpose
            baseStatestClass = Class.forName("org.bobba.SomeBaseClass");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return content.stream()
                .filter((Class<?> input) ->
                        baseStatestClass.isAssignableFrom(input) || getClassAnnotation(input, RunWith.class) != null)
                .collect(toList());
    }

    private static void sortBySimpleClassName(List<Class<?>> content) {
        content.sort(comparing(Class::getSimpleName));
    }

    private static void iterateClassesFromSameJar(Class<?> loggerClass, Consumer<Class<?>> classConsumer) {
        final ProtectionDomain protectionDomain = loggerClass.getProtectionDomain();
        final CodeSource codeSource = protectionDomain.getCodeSource();

        classIterate(codeSource.getLocation(), classConsumer);
    }

    private static void classIterate(URL sourceLocation, Consumer<Class<?>> classConsumer) {
        if (!"file".equals(sourceLocation.getProtocol())) {
            throw new RuntimeException("Unexpected protocol for source code url: " + sourceLocation);
        }

        final File file = new File(sourceLocation.getFile());
        if (!file.isFile()) {
            throw new RuntimeException("Source code location for the class is not a jar file: " + file);
        }

        final JarFile jarFile = openJarFile(file);

        final Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            final String name = jarEntry.getName();
            if (name.endsWith(".class")) {
                final String className = name.substring(0, name.length() - 6).replaceAll("/", ".");
                try {
                    final Class<?> aClass = Class.forName(className);
                    classConsumer.accept(aClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static JarFile openJarFile(File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Error when opening jar file: " + file, e);
        }
        return jarFile;
    }
}
