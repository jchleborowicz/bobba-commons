package org.bobba.tools.restRepeater;

public interface ClipboardService {

    String getClipboardContent();

    String[] listClipboardFiles();

    void pasteIntoClipboardFromFile(String fileName);

    void setClipboardContent(String text);

    void writeClipboardFile(String fileName);

    void deleteClipboardFile(String fileName);
}
