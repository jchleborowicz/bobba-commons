package org.bobba.tools.restRepeater.commands;

import org.apache.commons.lang3.StringUtils;
import org.bobba.tools.restRepeater.ClipboardService;
import org.bobba.tools.restRepeater.CommandLineUtils;
import org.bobba.tools.restRepeater.RestRepeater;
import org.bobba.tools.commandLine.Command;
import org.bobba.tools.commandLine.CommandLineBusinessException;
import org.bobba.tools.commandLine.CommandLineContext;
import org.bobba.tools.commandLine.CommandLineModule;
import org.bobba.tools.commandLine.CommandLineOutput;

@CommandLineModule(name = "rest")
public class RestModule {

    private final RestRepeater restRepeater;
    private final ClipboardService clipboardService;

    public RestModule(RestRepeater restRepeater, ClipboardService clipboardService) {
        this.restRepeater = restRepeater;
        this.clipboardService = clipboardService;
    }

    @Command(names = "e", description = "Executes request from clipboard")
    public void execute(String hostId, CommandLineContext context) {
        final String activeHost = getActiveHost(hostId, context);

        final String clipboard = clipboardService.getClipboardContent();

        restRepeater.parseAndSend(clipboard, activeHost);
    }

    private String getActiveHost(String hostId, CommandLineContext context) {
        final String activeHost;
        if (hostId == null) {
            activeHost = HostContextUtil.getActiveHost(context);
        } else {
            activeHost = HostContextUtil.getHostById(context, hostId);
        }

        if (activeHost == null) {
            throw new CommandLineBusinessException("Active host not defined. Use def-host command to define "
                    + "new host. Use select-host command to select active host.");
        }
        return activeHost;
    }

    @Command(names = "ef", description = "Execute file")
    public void execute(String fileName, String hostId, CommandLineContext context, CommandLineOutput output) {
        if (StringUtils.isBlank(fileName)) {
            throw new CommandLineBusinessException("Specify file name");
        }
        if (StringUtils.isEmpty(hostId)) {
            throw new CommandLineBusinessException("Please specify host as second parameter");
        }

        String activeHost = determineHost(hostId, context);

        output.println("Sending message to " + activeHost);
        final String message = CommandLineUtils.readFileContent(fileName);
        restRepeater.parseAndSend(message, hostId);
        output.println("Message sent");
    }

    private String determineHost(String hostId, CommandLineContext context) {
        final String result = HostContextUtil.getHostById(context, hostId);
        if (result == null) {
            return hostId;
        }
        return result;
    }

}
