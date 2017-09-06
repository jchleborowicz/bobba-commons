package org.bobba.tools.commandLine.commandline.commands;

import org.bobba.tools.commandLine.ClipboardService;
import org.bobba.tools.commandLine.CommandLineUtils;
import org.bobba.tools.commandLine.RestRepeater;
import org.bobba.tools.commandLine.commandline.Command;
import org.bobba.tools.commandLine.commandline.CommandLineBusinessException;
import org.bobba.tools.commandLine.commandline.CommandLineContext;
import org.bobba.tools.commandLine.commandline.CommandLineModule;
import org.bobba.tools.commandLine.commandline.CommandLineOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@CommandLineModule(name = "rest")
public class RestModule {

    @Autowired
    private RestRepeater restRepeater;
    @Autowired
    private ClipboardService clipboardService;

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
