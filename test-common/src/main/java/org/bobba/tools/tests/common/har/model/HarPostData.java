package org.bobba.tools.tests.common.har.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarPostData {

    private String mimeType;
    private String text;
    private List<HarParam> params = new ArrayList<HarParam>();

}
