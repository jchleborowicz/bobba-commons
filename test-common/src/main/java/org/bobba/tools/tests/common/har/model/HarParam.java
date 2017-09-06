package org.bobba.tools.tests.common.har.model;

import lombok.Data;

@Data
public class HarParam {

    private String name;
    private String value;

    @Override
    public String toString() {
        return name + "=" + value;
    }

}
