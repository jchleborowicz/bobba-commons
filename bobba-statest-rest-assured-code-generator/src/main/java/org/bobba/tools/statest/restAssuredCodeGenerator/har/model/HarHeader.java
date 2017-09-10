package org.bobba.tools.statest.restAssuredCodeGenerator.har.model;

import lombok.Data;

@Data
public class HarHeader {

    private String name;
    private String value;

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
