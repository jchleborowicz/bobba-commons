package org.bobba.tools.tests.common.har.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarLog {

    private String version;
    private HarCreator creator;
    private HarBrowser browser;
    private List<HarPage> pages = new ArrayList<HarPage>();
    private List<HarEntry> entries = new ArrayList<HarEntry>();

}
