package org.bobba.tools.restRepeater.commands;

import org.bobba.tools.commandLine.CommandLineBusinessException;
import org.bobba.tools.commandLine.CommandLineContext;

import java.util.HashMap;
import java.util.Map;

public final class HostContextUtil {

    public static final String ACTIVE_HOST_VARIABLE_NAME = "active-host";
    public static final String EXECUTE_HOSTS_VARIABLE_NAME = "execute-hosts";

    private HostContextUtil() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> readHosts(CommandLineContext context) {
        final Map<String, String> result = context.get(EXECUTE_HOSTS_VARIABLE_NAME, Map.class);
        return result == null ? new HashMap<>() : result;
    }

    public static String readActiveHostId(CommandLineContext context) {
        return context.get(ACTIVE_HOST_VARIABLE_NAME, String.class);
    }

    public static String getActiveHost(CommandLineContext context) {
        final String activeHostId = readActiveHostId(context);
        return activeHostId == null ? null : getHostById(context, activeHostId);
    }

    public static String getHostById(CommandLineContext context, String activeHostId) {
        final Map<String, String> hosts = readHosts(context);
        return hosts.get(activeHostId);
    }

    public static void setActiveHost(CommandLineContext context, String hostId) {
        final Map<String, String> hosts = readHosts(context);

        if (hosts == null || !hosts.containsKey(hostId)) {
            throw new CommandLineBusinessException("Host id not defined: " + hostId);
        }

        context.set(ACTIVE_HOST_VARIABLE_NAME, hostId);
    }
}
