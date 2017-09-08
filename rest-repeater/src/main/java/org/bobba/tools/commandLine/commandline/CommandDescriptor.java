package org.bobba.tools.commandLine.commandline;

import com.google.common.base.Joiner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class CommandDescriptor {

    private final String moduleName;
    private final Command commandAnnotation;
    private final Method method;
    private final Object command;
    private final List<ArgumentsFetcher> argumentsFetchers;

    public CommandDescriptor(String moduleName, Command commandAnnotation, Method method, Object command) {
        this.moduleName = notNull(moduleName);
        this.commandAnnotation = notNull(commandAnnotation);
        this.method = notNull(method);
        this.command = notNull(command);
        this.argumentsFetchers = createArgumentFetchers(method);
    }

    private static List<ArgumentsFetcher> createArgumentFetchers(Method method) {
        final List<ArgumentsFetcher> result = new ArrayList<>();
        int stringIndex = 0;
        for (Class<?> parameterType : method.getParameterTypes()) {
            result.add(createArgumentFetcher(parameterType, method, stringIndex));
            if (parameterType == String.class) {
                stringIndex++;
            }
        }
        return result;
    }

    private static ArgumentsFetcher createArgumentFetcher(Class<?> parameterType, Method method, final int stringIndex) {
        if (parameterType == String.class) {
            return (String[] arguments, CommandLineContext context, CommandLineOutput output) ->
                    stringIndex < arguments.length ? arguments[stringIndex] : null;
        }
        if (parameterType == CommandLineContext.class) {
            return (String[] arguments, CommandLineContext context, CommandLineOutput output) -> context;
        }
        if (parameterType == CommandLineOutput.class) {
            return (arguments, context, output) -> output;
        }
        if (parameterType == String[].class) {
            return (arguments, context, output) -> arguments;
        }
        throw new RuntimeException("Unexpected method parameter for module " + method.getDeclaringClass()
                + ", method " + method.getName() + ", parameter type: " + parameterType);
    }

    public String getModuleName() {
        return moduleName;
    }

    public Class<?> getCommandClass() {
        return command.getClass();
    }

    public String getDescription() {
        return this.commandAnnotation.description();
    }

    public String[] getNames() {
        return this.commandAnnotation.names();
    }

    public boolean isAppearsOnHistory() {
        return this.commandAnnotation.appearsOnHistory();
    }

    public void executeCommand(String[] arguments, CommandLineContext context, CommandLineOutput output) {
        try {
            final Object[] methodArguments = buildArguments(arguments, context, output);
            this.method.invoke(command, methodArguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    private Object[] buildArguments(String[] arguments, CommandLineContext context, CommandLineOutput output) {
        final Object[] result = new Object[this.argumentsFetchers.size()];
        for (int i = 0; i < result.length; i++) {
            final ArgumentsFetcher argumentsFetcher = argumentsFetchers.get(i);
            result[i] = argumentsFetcher.getArgument(arguments, context, output);
        }
        return result;
    }

    public String getCommandNamesAsString() {
        return Joiner.on(' ').join(this.getNames());
    }

    @FunctionalInterface
    private interface ArgumentsFetcher {
        Object getArgument(String[] arguments, CommandLineContext context, CommandLineOutput output);
    }

}
