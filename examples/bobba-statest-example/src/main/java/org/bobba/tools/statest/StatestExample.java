package org.bobba.tools.statest;

import org.bobba.tools.statest.common.junit.RestTest;
import org.bobba.tools.statest.common.junit.RestTestJUnitClassRunner;
import org.junit.runner.RunWith;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(RestTestJUnitClassRunner.class)
public class StatestExample {

    @RestTest(order = 1)
    public void countryGetAll() {
        expect()
            .statusCode(200)
            .header("Content-Type", "application/json;charset=UTF-8")
        .when()
            .get("http://services.groupkt.com/country/get/all")
        .then()
            .log().all();
    }

    @RestTest(order = 2)
    public void countryGetIso2CodeIN() {
        expect()
            .statusCode(200)
            .header("Content-Type", "application/json;charset=UTF-8")
        .when()
            .get("http://services.groupkt.com/country/get/iso2code/IN");
    }

    @RestTest(order = 3)
    public void countryGetIso3CodeIND() {
        expect()
            .statusCode(200)
            .header("Content-Type", "application/json;charset=UTF-8")
        .when()
            .get("http://services.groupkt.com/country/get/iso3code/IND");
    }

    @RestTest(order = 4)
    public void countrySearch() {
        expect()
            .statusCode(200)
            .header("Content-Type", "application/json;charset=UTF-8")
            .body("RestResponse", notNullValue())
            .body("RestResponse.messages", notNullValue())
            .body("RestResponse.messages.size()", is(2))
            .body("RestResponse.messages[0]", is("More webservices are available at http://www.groupkt.com/post/f2129b88/services.htm"))
            .body("RestResponse.messages[1]", is("Total [12] records found."))
            .body("RestResponse.result", notNullValue())
            .body("RestResponse.result.size()", is(12))
            .body("RestResponse.result[0]", notNullValue())
            .body("RestResponse.result[0].name", is("Brunei Darussalam"))
            .body("RestResponse.result[0].alpha2_code", is("BN"))
            .body("RestResponse.result[0].alpha3_code", is("BRN"))
            .body("RestResponse.result[1]", notNullValue())
            .body("RestResponse.result[1].name", is("Burundi"))
            .body("RestResponse.result[1].alpha2_code", is("BI"))
            .body("RestResponse.result[1].alpha3_code", is("BDI"))
            .body("RestResponse.result[2]", notNullValue())
            .body("RestResponse.result[2].name", is("Hungary"))
            .body("RestResponse.result[2].alpha2_code", is("HU"))
            .body("RestResponse.result[2].alpha3_code", is("HUN"))
            .body("RestResponse.result[3]", notNullValue())
            .body("RestResponse.result[3].name", is("Réunion"))
            .body("RestResponse.result[3].alpha2_code", is("RE"))
            .body("RestResponse.result[3].alpha3_code", is("REU"))
            .body("RestResponse.result[4]", notNullValue())
            .body("RestResponse.result[4].name", is("Saint Helena, Ascension and Tristan da Cunha"))
            .body("RestResponse.result[4].alpha2_code", is("SH"))
            .body("RestResponse.result[4].alpha3_code", is("SHN"))
            .body("RestResponse.result[5]", notNullValue())
            .body("RestResponse.result[5].name", is("Tanzania, United Republic of"))
            .body("RestResponse.result[5].alpha2_code", is("TZ"))
            .body("RestResponse.result[5].alpha3_code", is("TZA"))
            .body("RestResponse.result[6]", notNullValue())
            .body("RestResponse.result[6].name", is("Tunisia"))
            .body("RestResponse.result[6].alpha2_code", is("TN"))
            .body("RestResponse.result[6].alpha3_code", is("TUN"))
            .body("RestResponse.result[7]", notNullValue())
            .body("RestResponse.result[7].name", is("United Arab Emirates"))
            .body("RestResponse.result[7].alpha2_code", is("AE"))
            .body("RestResponse.result[7].alpha3_code", is("ARE"))
            .body("RestResponse.result[8]", notNullValue())
            .body("RestResponse.result[8].name", is("United Kingdom of Great Britain and Northern Ireland"))
            .body("RestResponse.result[8].alpha2_code", is("GB"))
            .body("RestResponse.result[8].alpha3_code", is("GBR"))
            .body("RestResponse.result[9]", notNullValue())
            .body("RestResponse.result[9].name", is("United States of America"))
            .body("RestResponse.result[9].alpha2_code", is("US"))
            .body("RestResponse.result[9].alpha3_code", is("USA"))
            .body("RestResponse.result[10]", notNullValue())
            .body("RestResponse.result[10].name", is("United States Minor Outlying Islands"))
            .body("RestResponse.result[10].alpha2_code", is("UM"))
            .body("RestResponse.result[10].alpha3_code", is("UMI"))
            .body("RestResponse.result[11]", notNullValue())
            .body("RestResponse.result[11].name", is("Wallis and Futuna"))
            .body("RestResponse.result[11].alpha2_code", is("WF"))
            .body("RestResponse.result[11].alpha3_code", is("WLF"))
        .when()
            .get("http://services.groupkt.com/country/search?text=un");
    }

}
