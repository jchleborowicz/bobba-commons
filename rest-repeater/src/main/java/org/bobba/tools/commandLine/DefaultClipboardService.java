package org.bobba.tools.commandLine;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

@Component
public class DefaultClipboardService implements ClipboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClipboardService.class);

    private static final File BASE_DIRECTORY = new File("clipboard").getAbsoluteFile();
    private static final String CLIPBOARD_FILE_POSTFIX = ".clipboard";

    @PostConstruct
    public void initialize() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("DefaultClipboardService base directory set to " + BASE_DIRECTORY);
        }
    }

    @Override
    public String getClipboardContent() {
        try {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            final Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
            return (String) systemClipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] listClipboardFiles() {
        final String[] result = BASE_DIRECTORY.list((File dir, String name) -> name.endsWith(CLIPBOARD_FILE_POSTFIX));
        if (result == null) {
            return new String[]{};
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].substring(0, result[i].length() - CLIPBOARD_FILE_POSTFIX.length());
        }
        return result;
    }

    @Override
    public void pasteIntoClipboardFromFile(String fileName) {
        final File inputFile = getRequiredFile(fileName);

        final String fileContent = CommandLineUtils.readFileContent(inputFile);

        setClipboardContent(fileContent);
    }

    @Override
    public void setClipboardContent(String text) {
        final StringSelection selection = new StringSelection(text);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Override
    public void writeClipboardFile(String fileName) {
        final File file = getClipboardFile(fileName);

        final String clipboardContent = getClipboardContent();
        try {
            FileUtils.write(file, clipboardContent);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Clipboard content was written into file " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error when writing clipboard file", e);
        }
    }

    @Override
    public void deleteClipboardFile(String fileName) {
        final File fileToDelete = getRequiredFile(fileName);
        if (!fileToDelete.delete()) {
            throw new RuntimeException("Could not remove file " + fileToDelete);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Clipboard file has been removed: " + fileToDelete);
        }
    }

    private File getRequiredFile(String fileName) {
        final File inputFile = getClipboardFile(fileName);

        if (!inputFile.exists()) {
            throw new ClipboardServiceException("File " + inputFile + " does not exist");
        }
        if (!inputFile.isFile()) {
            throw new ClipboardServiceException(inputFile + " is not a file");
        }
        return inputFile;
    }

    private File getClipboardFile(String fileName) {
        return new File(BASE_DIRECTORY, fileName + CLIPBOARD_FILE_POSTFIX);
    }

}
