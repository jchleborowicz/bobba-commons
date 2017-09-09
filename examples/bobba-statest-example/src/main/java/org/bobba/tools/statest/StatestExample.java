package org.bobba.tools.statest;

import org.bobba.tools.statest.common.junit.Statest;
import org.bobba.tools.statest.common.junit.StatestJUnitClassRunner;
import org.bobba.tools.statest.common.junit.TestState;
import org.junit.runner.RunWith;

import static com.jayway.restassured.RestAssured.expect;

@RunWith(StatestJUnitClassRunner.class)
public class StatestExample {

    @Statest(order = 1, storeResultIn = "countryAlpha2Code")
    public String countryGetAll() {
        return
            expect()
                .statusCode(200)
                .header("Content-Type", "application/json;charset=UTF-8")
            .when()
                .get("http://services.groupkt.com/country/get/all")
            .then()
                .log().all()
            .extract()
                .body().path("RestResponse.result[0].alpha2_code");
    }

    @Statest(order = 2, storeResultIn = "countryName")
    public String countryGetIso2CodeIN(@TestState(objectId = "countryAlpha2Code") String countryAlpha2Code) {
        return
            expect()
                .statusCode(200)
                .header("Content-Type", "application/json;charset=UTF-8")
            .when()
                .get("http://services.groupkt.com/country/get/iso2code/" + countryAlpha2Code)
            .then()
                .log().all()
            .extract()
                .body().path("RestResponse.result.name");
    }

    @Statest(order = 3)
    public void countrySearch(@TestState(objectId = "countryName") String countryName) {
        expect()
            .statusCode(200)
            .header("Content-Type", "application/json;charset=UTF-8")
        .when()
            .get("http://services.groupkt.com/country/search?text=" + countryName)
        .then()
            .log().all();
    }

}
