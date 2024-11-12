package dev.bakulin.ticktacktoe;

import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GamePlayApiTest extends BaseIntegrationTest {

    @DisplayName("check for list of games response")
    @Test
    void testListGames() {
        String s = given()
                .get(buildUrl("/gameplay/list"))
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response().asString();

        assertEquals("[]", s);
    }

    @DisplayName("check game was init")
    @Test
    void testInitGame() throws JSONException {
        String s = given()
                .post(buildUrl("/gameplay/init"))
                .then()
                .log().body()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response()
                .asString();

        assertNotNull(s);
        JSONComparator com = new CustomComparator(JSONCompareMode.STRICT,
                new Customization("session", (o1, o2) -> true),
                new Customization("createdAt", (o1, o2) -> true),
                new Customization("updatedAt", (o1, o2) -> true));

        JSONAssert.assertEquals(INIT_JSON, s, com);
    }

    String INIT_JSON = """
            {
            "session": "ignore",
            "state": {
                "status": "INIT",
                "field": "123|456|789|",
                "crossesMoves": "00000",
                "zeroesMoves": "00000"
            },
            "opponent": null,
            "createdAt": "ignore",
            "updatedAt": "ignore"
        }
        """;

    @DisplayName("check game status by session")
    @Test
    void testGameStatus() throws JSONException {
        String session = given()
                .post(buildUrl("/gameplay/init"))
                .then().statusCode(200)
                .extract().path("session");

        String actual = given()
                .header("X-SESSION-ID", session)
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        assertNotNull(actual);
        JSONComparator com = new CustomComparator(JSONCompareMode.STRICT,
                new Customization("session", (o1, o2) -> true),
                new Customization("createdAt", (o1, o2) -> true),
                new Customization("updatedAt", (o1, o2) -> true));

        JSONAssert.assertEquals(INIT_JSON, actual, com);
    }

    @DisplayName("check game status by session")
    @Test
    void testGameStatusError() throws JSONException {
        given()
                .header("X-SESSION-ID", "incorrect")
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(500);
    }

    @DisplayName("check game status session header required")
    @Test
    void testGameStatusValidation() throws JSONException {
        given()
                 //.header("X-SESSION-ID", "required")
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(400);
    }
}
