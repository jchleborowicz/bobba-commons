package org.bobba.tools.commandLine.commandline;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public class CommandRequest implements Serializable {

    private final String name;
    private final String[] arguments;
    private final int hashCode;

    public CommandRequest(String name, String[] arguments) {
        this.name = notEmpty(name);
        this.arguments = notNull(arguments);
        this.hashCode = name.hashCode() + Arrays.hashCode(this.arguments);
    }

    public static CommandRequest parse(String line) {
        final String[] split = line.split("\\s+");
        final String commandName = split[0];
        final String[] arguments = split.length == 1 ? new String[]{} : ArrayUtils.subarray(split, 1, split.length);

        return new CommandRequest(commandName, arguments);
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments.clone();
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CommandRequest)) {
            return false;
        }
        final CommandRequest other = (CommandRequest) obj;
        return this.name.equals(other.name) && Arrays.equals(this.arguments, other.arguments);
    }

    @Override
    public String toString() {
        return this.arguments.length == 0 ? this.name : this.name + " " + Joiner.on(" ").join(arguments);
    }

}
