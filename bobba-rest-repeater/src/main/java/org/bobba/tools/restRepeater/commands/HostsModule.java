package org.bobba.tools.restRepeater.commands;

import com.google.common.base.Joiner;
import org.bobba.tools.commandLine.Command;
import org.bobba.tools.commandLine.CommandLineBusinessException;
import org.bobba.tools.commandLine.CommandLineContext;
import org.bobba.tools.commandLine.CommandLineModule;
import org.bobba.tools.commandLine.CommandLineOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandLineModule(name = "hosts")
public class HostsModule {

    @Command(names = {"sh", "set-host"}, description = "Sets active host")
    public void execute(String hostId, CommandLineOutput output, CommandLineContext context) {
        if (hostId == null) {
            throw new CommandLineBusinessException("Provide host id as command parameter");
        }

        HostContextUtil.setActiveHost(context, hostId);

        output.println("Host selected: " + HostContextUtil.getActiveHost(context));
    }

    @Command(names = {"dh", "def-host"}, description = "Define hosts for rest executions")
    public void execute(String hostId, String hostName, CommandLineContext context, CommandLineOutput output) {
        if (hostId == null || hostName == null) {
            throw new CommandLineBusinessException("Expected two arguments: host id and host name with port\n"
                    + "example command:\ndef-host h1 localhost:8081");
        }

        final Map<String, String> hosts = getHostsMap(context);

        hosts.put(hostId, hostName);

        context.set(HostContextUtil.EXECUTE_HOSTS_VARIABLE_NAME, hosts);

        output.println(String.format("Host defined - %s : %s", hostId, hostName));
    }

    @Command(names = {"ph", "print-hosts"}, description = "Prints hosts")
    public void execute(CommandLineOutput output, CommandLineContext context) {
        final Map<String, String> hosts = HostContextUtil.readHosts(context);
        final String activeHost = HostContextUtil.getActiveHost(context);
        if (activeHost == null) {
            output.println("Active host is not defined");
        } else {
            output.println("Active host: " + activeHost);
        }

        output.println("Host list:");
        if (isEmpty(hosts)) {
            output.println("No hosts defined");
        } else {
            output.println(toString(hosts));
        }
    }

    private Map<String, String> getHostsMap(CommandLineContext context) {
        //noinspection unchecked
        final Map<String, String> result = context.get(HostContextUtil.EXECUTE_HOSTS_VARIABLE_NAME, Map.class);
        return result == null ? new HashMap<>() : result;
    }

    private String toString(Map<String, String> hosts) {
        final List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : hosts.entrySet()) {
            result.add(" - " + entry.getKey() + " - " + entry.getValue());
        }
        Collections.sort(result);
        return Joiner.on('\n').join(result);
    }

    private static boolean isEmpty(Map hostVariable) {
        return hostVariable == null || hostVariable.isEmpty();
    }

}
