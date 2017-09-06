package org.bobba.tools.commandLine.commandline;

import jline.console.ConsoleReader;

import java.io.IOException;

import static org.apache.commons.lang3.Validate.notNull;

public class ConsoleReaderCommandLineOutput implements CommandLineOutput {

    private final ConsoleReader console;

    public ConsoleReaderCommandLineOutput(ConsoleReader console) {
        this.console = notNull(console);
    }

    @Override
    public void println(String text) {
        try {
            this.console.println(text);
            this.console.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error when printing to console", e);
        }
    }

}
