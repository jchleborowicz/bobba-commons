package org.bobba.tools.commandLine.commandline;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import jline.console.ConsoleReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RestRepeaterCommandLine {

    private static final String COMMAND_LINE_CONTEXT_FILE = ".cmd-line-ctx";

    private final ConsoleReader console;
    private final CommandLineOutput commandLineOutput;
    private final ApplicationContext beanFactory;

    private ImmutableMap<String, CommandDescriptor> commands;
    private CommandLineContext commandLineContext;

    public RestRepeaterCommandLine(ConsoleReader console, CommandLineOutput commandLineOutput,
                                   ApplicationContext beanFactory) {
        this.console = console;
        this.commandLineOutput = commandLineOutput;
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void initialize() {
        this.commands = CommandMapBuilder.newInstance()
                .with("system", new SystemModule())
                .with(beanFactory.getBeansWithAnnotation(CommandLineModule.class).values())
                .build();
        this.commandLineContext = readCommandLineContextFromFile(commandLineOutput);
    }

    private static CommandLineContext readCommandLineContextFromFile(CommandLineOutput commandLineOutput) {
        final File commandLineContextFile = new File(COMMAND_LINE_CONTEXT_FILE).getAbsoluteFile();
        if (commandLineContextFile.exists() && commandLineContextFile.isFile()) {
            commandLineOutput.println("Reading context form file: " + commandLineContextFile);
            try {
                final ObjectInputStream objectInputStream =
                        new ObjectInputStream(new FileInputStream(commandLineContextFile));
                final Object result = objectInputStream.readObject();
                objectInputStream.close();

                return (CommandLineContext) result;
            } catch (IOException e) {
                commandLineOutput.println("Error when reading context file: " + commandLineContextFile);
                return new CommandLineContext();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        return new CommandLineContext();
    }

    private void writeCommandLineContextToFile() {
        final File commandLineContextFile = new File(COMMAND_LINE_CONTEXT_FILE).getAbsoluteFile();
        commandLineOutput.println("Writing context to file " + commandLineContextFile);
        try {
            final ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(new FileOutputStream(commandLineContextFile));
            objectOutputStream.writeObject(this.commandLineContext);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws IOException {
        printGreeting();
        printPrompt();

        String line;
        while ((line = console.readLine()) != null) {
            try {
                onCommand(line);
            } catch (ExitCommandLineException e) {
                if (StringUtils.isNotBlank(e.getMessage())) {
                    commandLineOutput.println(e.getMessage());
                }
                break;
            }

            printPrompt();
        }

        writeCommandLineContextToFile();
    }

    private void printGreeting() throws IOException {
        commandLineOutput.println("Welcome to Rest Repeater!");
        commandLineOutput.println("Use help command to display help");
    }

    private void onCommand(String line) throws IOException {
        if (StringUtils.isNotBlank(line)) {
            final CommandRequest command = CommandRequest.parse(line);

            if ("re".equals(command.getName())) {
                executeHistoryCommand(command.getArguments());
            } else {
                executeCommand(command);
            }
        }
    }

    private void executeHistoryCommand(String[] arguments) {
        final int commandIndex;
        if (arguments.length == 0) {
            commandIndex = 0;
        } else if (arguments.length == 1) {
            if (StringUtils.isNumeric(arguments[0])) {
                commandIndex = Integer.parseInt(arguments[0]);
            } else {
                throw new CommandLineBusinessException(
                        "Command parameter should reference command number in history. See history command");
            }
        } else {
            throw new CommandLineBusinessException("Only one parameter is accepted for re command");
        }

        if (commandIndex < 0 || commandIndex >= commandLineContext.getCommandHistory().size()) {
            throw new CommandLineBusinessException("Incorrect history command index: " + commandIndex);
        }

        final CommandRequest commandRequest = commandLineContext.getCommandHistory().get(commandIndex);

        commandLineOutput.println("Executing command: " + commandRequest);

        executeCommand(commandRequest);
    }

    private void executeCommand(CommandRequest commandRequest) {
        try {
            final CommandDescriptor commandDescriptor = getCommandByName(commandRequest.getName());

            if (commandDescriptor.isAppearsOnHistory()) {
                commandLineContext.registerCommand(commandRequest);
            }

            commandDescriptor.executeCommand(commandRequest.getArguments(), commandLineContext, commandLineOutput);
        } catch (ExitCommandLineException e) {
            throw e;
        } catch (CommandLineBusinessException e) {
            commandLineOutput.println(e.getMessage());
        } catch (Throwable e) {
            commandLineOutput.println("Exception when executing command: " + commandRequest.toString());
            commandLineOutput.println(ExceptionUtils.getStackTrace(e));
        }
    }

    private CommandDescriptor getCommandByName(String commandName) {
        final CommandDescriptor result = commands.get(commandName);

        if (result == null) {
            throw new CommandLineBusinessException("Command does not exist: " + commandName);
        }

        return result;
    }

    private void printPrompt() throws IOException {
        console.print(">");
        console.flush();
    }

    private final static class CommandMapBuilder {
        private Map<String, CommandDescriptor> commands = new HashMap<>();

        private CommandMapBuilder() {
        }

        public static CommandMapBuilder newInstance() {
            return new CommandMapBuilder();
        }

        public CommandMapBuilder with(String moduleName, Object command) {
            final Class<?> commandClass = command.getClass();
            final Method[] methods = commandClass.getMethods();
            for (Method method : methods) {
                final Command commandAnnotation = method.getAnnotation(Command.class);
                if (commandAnnotation != null) {
                    registerCommandMethod(moduleName, command, method, commandAnnotation);
                }
            }

            return this;
        }

        private void registerCommandMethod(String moduleName, Object command, Method method,
                                           Command commandAnnotation) {
            final CommandDescriptor commandDescriptor =
                    createCommandDescriptor(moduleName, commandAnnotation, method, command);
            for (String commandName : commandDescriptor.getNames()) {
                if (commands.containsKey(commandName)) {
                    throw new RuntimeException(String.format("Duplicated command name: %s, Command classes:\n%s,\n%s",
                            commandName, commands.get(commandName).getCommandClass(), command.getClass()));
                }
                commands.put(commandName, commandDescriptor);
            }
        }

        private CommandDescriptor createCommandDescriptor(String moduleName, Command commandAnnotation, Method method,
                                                          Object command) {
            return new CommandDescriptor(moduleName, commandAnnotation, method, command);
        }

        public CommandMapBuilder with(Collection<Object> commands) {
            for (Object command : commands) {
                final CommandLineModule moduleAnnotation = command.getClass().getAnnotation(CommandLineModule.class);
                if (moduleAnnotation != null) {
                    with(moduleAnnotation.name(), command);
                }
            }
            return this;
        }

        public ImmutableMap<String, CommandDescriptor> build() {
            return ImmutableMap.copyOf(commands);
        }
    }

    private class SystemModule {

        @Command(names = {"h", "help"}, description = "Prints command list", appearsOnHistory = false)
        public void execute(String commandName, CommandLineOutput output) {
            if (commandName == null) {
                printListOfCommands(output);
            } else {
                printCommandHelp(commandName, output);
            }
        }

        private void printCommandHelp(String commandName, CommandLineOutput output) {
            final CommandDescriptor command = getCommandDescriptorByCommandName(commandName);
            if (command == null) {
                throw new CommandLineBusinessException("Command " + commandName + " does not exist");
            } else {
                output.println("Help for command: " + command.getCommandNamesAsString());
                output.println(command.getDescription());
            }

        }

        private CommandDescriptor getCommandDescriptorByCommandName(String commandName) {
            return RestRepeaterCommandLine.this.commands.get(commandName);
        }

        private void printListOfCommands(CommandLineOutput output) {
            output.println("Available commands:");

            final List<String> help = new ArrayList<>();
            final Set<CommandDescriptor> commandDescriptors = getUniqueCommandDescriptors();
            for (CommandDescriptor command : commandDescriptors) {
                final String commandNamesList = Joiner.on(", ").join(command.getNames());
                final String simpleDescription = command.getDescription().split("\n")[0];
                help.add(String.format(" - %s - %s", commandNamesList, simpleDescription));
            }

            Collections.sort(help);

            output.println(Joiner.on('\n').join(help));
        }

        private HashSet<CommandDescriptor> getUniqueCommandDescriptors() {
            return new HashSet<>(RestRepeaterCommandLine.this.commands.values());
        }

        @Command(names = {"exit", "quit", "q"}, description = "Exits command line", appearsOnHistory = false)
        public void execute() {
            throw new ExitCommandLineException("Bye, bye");
        }

        @Command(names = {"hi", "history"}, description = "Print history", appearsOnHistory = false)
        public void execute(CommandLineOutput output, CommandLineContext context) {
            final List<CommandRequest> commandHistory = context.getCommandHistory();
            if (commandHistory.isEmpty()) {
                output.println("Command history is empty");
            } else {
                output.println("Command history:");
                for (int i = commandHistory.size() - 1; i >= 0; i--) {
                    output.println(i + ": " + commandHistory.get(i));
                }
            }
        }

    }

}
