package dev.bakulin.ticktacktoe;

import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GamePlayApiTest extends BaseIntegrationTest {

    private final CustomComparator GAME_STATE_COMPARATOR = new CustomComparator(JSONCompareMode.STRICT,
            new Customization("session", (o1, o2) -> true),
            new Customization("createdAt", (o1, o2) -> true),
            new Customization("updatedAt", (o1, o2) -> true));

    @DisplayName("check for list of games response")
    @Test
    void testListGames() {
        String s = given()
                .get(buildUrl("/gameplay/list"))
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract().response().asString();

        assertEquals("[]", s);
    }

    @DisplayName("check game was init")
    @Test
    void testInitGame() throws JSONException {
        String actual = given()
                .post(buildUrl("/gameplay/init"))
                .then()
                .log().body()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        JSONAssert.assertEquals(fromFile("/response/game_play_init.json"), actual, GAME_STATE_COMPARATOR);
    }

    @DisplayName("check game state by session in json")
    @Test
    void testGameStateJson() throws JSONException {
        String session = initSession();

        String actual = given()
                .header("X-SESSION-ID", session)
                .contentType(ContentType.JSON)
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        JSONAssert.assertEquals(fromFile("/response/game_play_init.json"), actual, GAME_STATE_COMPARATOR);
    }

    @DisplayName("check game state by session in formatted text")
    @Test
    void testGameStateText() throws JSONException {
        String session = initSession();

        String actual = given()
                .header("X-SESSION-ID", session)
                .contentType(ContentType.TEXT)
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        assertEquals(fromFile("/response/game_state_init.txt"), actual);
    }

    @DisplayName("check game status unknown session error")
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
    void testGameStatusHeaderValidation() throws JSONException {
        given()
                 //.header("X-SESSION-ID", "required")
                .get(buildUrl("/gameplay/state"))
                .then()
                .log().body()
                .statusCode(400);
    }

    @DisplayName("check first move after init")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            # moveBy | moveTo | field     | crosses | zeroes
            CROSS    | 1      | X23456789 | 10000   | 00000
            CROSS    | 5      | 1234X6789 | 50000   | 00000
            ZERO     | 1      | O23456789 | 00000   | 10000
            ZERO     | 5      | 1234O6789 | 00000   | 50000
            """)
    void testMoveAfterInit(String moveBy, Integer moveTo, String field, String crosses, String zeroes) throws JSONException {
        String session = initSession();

        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", moveBy)
                .replace("{moveTo}", moveTo.toString());

        String actual = given()
                .header("X-SESSION-ID", session)
                .body(request)
                .contentType(ContentType.JSON)
                .post(buildUrl("/gameplay/move"))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/after_move_template.json")
                .replace("{field}", field)
                .replace("{crosses}", crosses)
                .replace("{zeroes}", zeroes);
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @DisplayName("check first move by zero is not in his turn")
    @Test
    void testNotInTurnMoveAfterInit() throws JSONException {
        String session = initSession();

        String actual = given()
                .header("X-SESSION-ID", session)
                .body(fromFile("/request/first_move_by_zero.json"))
                .contentType(ContentType.JSON)
                .post(buildUrl("/gameplay/move"))
                .then()
                .log().body()
                .statusCode(400)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/error.json");
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @DisplayName("check first move validate request")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            # name         | jsonRequest                     | expectMessage
            moveTo missed  | {"moveBy":"CROSS"}              | moveTo: required
            invalid moveBy | {"moveBy":"WHO", "moveTo":1}    | moveBy: required CROSS or ZERO
            moveBy missed  | {"moveTo":1},                   | moveBy: required CROSS or ZERO
            invalid moveTo | {"moveBy":"CROSS", "moveTo":0}  | moveTo: required in interval 1..9
            invalid moveTo | {"moveBy":"CROSS", "moveTo":10} | moveTo: required in interval 1..9
            """)
    void testValidateMoveRequest(String name, String jsonRequest, String expectMessage) throws JSONException {
        String session = initSession();

        String actual = given()
                .header("X-SESSION-ID", session)
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .post(buildUrl("/gameplay/move"))
                .then()
                .log().body()
                .statusCode(400)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/error.json")
                .replace("{error}", "Request validation error")
                .replace("{message}", expectMessage)
                ;
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @DisplayName("check first move session header required")
    @Test
    void testMoveHeaderValidation() throws JSONException {
        given()
                //.header("X-SESSION-ID", "required")
                .post(buildUrl("/gameplay/move"))
                .then()
                .log().body()
                .statusCode(400);
    }

    private String initSession() {
        return given()
                .post(buildUrl("/gameplay/init"))
                .then().statusCode(200)
                .extract().path("session");
    }

}
