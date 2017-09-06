package org.bobba.tools.commandLine;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class CommandLineUtils {

    private CommandLineUtils() {
    }

    public static String readFileContent(String fileName) {
        return readFileContent(new File(fileName));
    }

    public static String readFileContent(File inputFile) {
        try {
            return FileUtils.readFileToString(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
