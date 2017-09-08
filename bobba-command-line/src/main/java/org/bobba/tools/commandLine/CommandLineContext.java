package org.bobba.tools.commandLine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLineContext implements Serializable {

    private static final int MAX_COMMAND_HISTORY_SIZE = 10;

    private final Map<String, Object> variables = new HashMap<>();
    private final List<CommandRequest> commandHistory = new ArrayList<>();

    public void set(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {
        return variables.get(name);
    }

    public <T> T get(String name, Class<T> expectedType) {
        final Object result = variables.get(name);
        if (result == null) {
            return null;
        }
        if (!expectedType.isInstance(result)) {
            throw new RuntimeException(String.format("Variable is not of expected type. "
                            + "Variable name: %s, Variable type: %s, Expected variable type: %s",
                    name, result.getClass(), expectedType));
        }
        //noinspection unchecked
        return (T) result;
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public void registerCommand(CommandRequest command) {
        removeCommand(command);

        insertCommand(command);

        trimCommandList();
    }

    private void trimCommandList() {
        while (commandHistory.size() > MAX_COMMAND_HISTORY_SIZE) {
            commandHistory.remove(commandHistory.size() - 1);
        }
    }

    private void insertCommand(CommandRequest command) {
        commandHistory.add(0, command);
    }

    private void removeCommand(CommandRequest command) {
        commandHistory.removeIf(command::equals);
    }

    public List<CommandRequest> getCommandHistory() {
        return commandHistory;
    }
}
