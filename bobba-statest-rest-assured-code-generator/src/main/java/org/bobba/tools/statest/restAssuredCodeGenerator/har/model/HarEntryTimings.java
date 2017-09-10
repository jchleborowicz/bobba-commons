package org.bobba.tools.statest.restAssuredCodeGenerator.har.model;

import lombok.Data;

@Data
public class HarEntryTimings {

    private Double blocked;
    private Double dns;
    private Double connect;
    private Double send;
    private Double wait;
    private Double receive;
    private Double ssl;

}
