package org.bobba.tools.commandLine.commandline.commands;

import org.bobba.tools.commandLine.ClipboardService;
import org.bobba.tools.commandLine.commandline.Command;
import org.bobba.tools.commandLine.commandline.CommandLineModule;
import org.bobba.tools.commandLine.commandline.CommandLineOutput;
import org.springframework.beans.factory.annotation.Autowired;

@CommandLineModule(name = "clipboard")
public class ClipboardModule {

    private final ClipboardService clipboardService;

    public ClipboardModule(ClipboardService clipboardService) {
        this.clipboardService = clipboardService;
    }

    @Command(names = "c", description = "Clipboard manipulation: printing, saving and reading to/from file.\n"
            + "When used without parameters prints clipboard content\n"
            + "When used with parameters:\n"
            + "c l                  - list clipboard files\n"
            + "c w file-name        - saves clipboard content to clipboard file\n"
            + "c r file-name        - reads clipboard file into clipboard")
    public void execute(String subcommand, String subcommandArgument, CommandLineOutput output) {
        if (subcommand == null) {
            printClipboard(output);
        } else {
            executeSubcommand(subcommand, subcommandArgument, output);
        }
    }

    private void printClipboard(CommandLineOutput output) {
        output.println("Printing clipboard:");
        output.println(clipboardService.getClipboardContent());
    }

    private void executeSubcommand(String subcommand, String subcommandArgument, CommandLineOutput output) {
        if (subcommand.equals("w")) {
            writeClipboardFile(subcommandArgument, output);
        } else if (subcommand.equals("l")) {
            printClipboardFiles(output);
        } else if (subcommand.equals("r")) {
            readFileIntoClipboard(subcommandArgument, output);
        } else if (subcommand.equals("d")) {
            deleteClipboardFile(subcommandArgument, output);
        }
    }

    private void deleteClipboardFile(String fileName, CommandLineOutput output) {
        clipboardService.deleteClipboardFile(fileName);
        output.println("Clipboard file " + fileName + " has been deleted");
    }

    private void printClipboardFiles(CommandLineOutput output) {
        final String[] files = clipboardService.listClipboardFiles();
        output.println("Clipboard files list:");
        for (String file : files) {
            output.println("  " + file);
        }
    }

    private void readFileIntoClipboard(String subcommandArgument, CommandLineOutput output) {
        clipboardService.pasteIntoClipboardFromFile(subcommandArgument);
        output.println("Clipboard content read from file " + subcommandArgument + ":");
        output.println(clipboardService.getClipboardContent());
    }

    private void writeClipboardFile(String fileName, CommandLineOutput output) {
        clipboardService.writeClipboardFile(fileName);
        output.println("Clipboard content was written into file " + fileName);
    }

}
