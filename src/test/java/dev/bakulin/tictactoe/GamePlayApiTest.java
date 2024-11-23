package dev.bakulin.tictactoe;

import dev.bakulin.tictactoe.model.Actor;
import io.restassured.http.ContentType;
import org.json.JSONException;
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

    @Test
    void testListGames() {
        given()
                .get(buildUrl("/gameplay/list"))
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', nullValues = "<null>", textBlock = """
            # requestSide | guest | host  | nextMoveBy
            ZERO          | ZERO  | CROSS | CROSS
            CROSS         | CROSS | ZERO  | CROSS
            <null>        | ZERO  | CROSS | CROSS
            """)
    void testInitGame(String requestSide, String guest, String host, String nextMoveBy) throws JSONException {
        String actual = given()
                .log().body()
                .body("{\"side\": %s}".formatted(requestSide == null ? null : "\""+requestSide+"\""))
                .contentType(ContentType.JSON)
                .post(buildUrl("/gameplay/init"))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/game_play_init_template.json")
                .replace("{host}", host)
                .replace("{guest}", guest)
                .replace("{next}", nextMoveBy);
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @Test
    void testGameStateJson() throws JSONException {
        String session = initSession();

        String actual = given()
                .accept(ContentType.JSON)
                .get(buildUrl("/gameplay/state/" + session))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        JSONAssert.assertEquals(fromFile("/response/game_play_init.json"), actual, GAME_STATE_COMPARATOR);
    }

    @Test
    void testGameStateText() throws JSONException {
        String session = initSession();

        String actual = given()
                .accept(ContentType.TEXT)
                .get(buildUrl("/gameplay/state/" + session))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        assertEquals(fromFile("/response/game_state_init.txt"), actual);
    }

    @Test
    void testGameStatusError() throws JSONException {
        String actual = given()
                .get(buildUrl("/gameplay/state/incorrect"))
                .then()
                .log().body()
                .statusCode(404)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/error.json")
                .replace("{error}", "Game not found")
                .replace("{message}", "Game incorrect is not found");
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            # initBy | moveTo | field     | crosses | zeroes
            CROSS    | 1      | X23456789 | 100000000   | 000000000
            CROSS    | 5      | 1234X6789 | 000010000   | 000000000
            """)
    void testCrossMoveAfterInit(String initBy, Integer moveTo, String field, String crosses, String zeroes) throws JSONException {
        String session = initSession(initBy);

        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", initBy)
                .replace("{moveTo}", moveTo.toString());

        String actual = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post(buildUrl("/gameplay/move/" + session))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/after_move_template.json")
                .replace("{round}", "1")
                .replace("{field}", field)
                .replace("{crosses}", crosses)
                .replace("{zeroes}", zeroes)
                .replace("{next}", "CROSS".equals(initBy) ? "ZERO" : "CROSS")
                .replace("{host}", "CROSS".equals(initBy) ? "ZERO" : "CROSS")
                .replace("{guest}", initBy)
                ;

        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            # moveTo | field     | zeroes
            2        | XO3456789 | 010000000
            5        | X234O6789 | 000010000
            """)
    void testZeroMoveAfterInit(Integer moveTo, String field, String zeroes) throws JSONException {
        String session = initSession();

        String crossRequest = fromFile("/request/move_template.json")
                .replace("{moveBy}", "CROSS")
                .replace("{moveTo}", "1");

        given()
                .body(crossRequest)
                .contentType(ContentType.JSON)
                .log().body()
                .post(buildUrl("/gameplay/move/" + session))
                .then()
                .log().body()
                .statusCode(200);

        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", "ZERO")
                .replace("{moveTo}", moveTo.toString());

        String actual = given()
                .body(request)
                .contentType(ContentType.JSON)
                .log().body()
                .post(buildUrl("/gameplay/move/" + session))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/after_move_template.json")
                .replace("{round}", "2")
                .replace("{field}", field)
                .replace("{crosses}", "100000000")
                .replace("{zeroes}", zeroes)
                .replace("{next}", "CROSS")
                .replace("{host}", "CROSS")
                .replace("{guest}", "ZERO")
                ;

        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @Test
    void testNotInTurnMoveAfterInit() throws JSONException {
        String session = initSession();

        String actual = given()
                .contentType(ContentType.JSON)
                .body(fromFile("/request/first_move_by_zero.json"))
                .post(buildUrl("/gameplay/move/" + session))
                .then()
                .log().body()
                .statusCode(400)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/error.json")
                .replace("{error}", "Move rejected")
                .replace("{message}", "Next move expected by CROSS");
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

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
                .contentType(ContentType.JSON)
                .body(jsonRequest)
                .post(buildUrl("/gameplay/move/" + session))
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

    @Test
    void testMoveSessionValidation() throws JSONException {
        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", "CROSS")
                .replace("{moveTo}", "3");
        String actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(buildUrl("/gameplay/move/incorrect"))
                .then()
                .log().body()
                .statusCode(404)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/error.json")
                .replace("{error}", "Game not found")
                .replace("{message}", "Game incorrect is not found");
        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @Test
    void testFullGameplayWinByCross() {
        String s = initSession();
        makeCrossMove(1, s);
        makeZeroMove(4, s);
        makeCrossMove(2, s);
        makeZeroMove(5, s);

        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", "CROSS")
                .replace("{moveTo}", "3");

        String actual = given()
                .body(request)
                .contentType(ContentType.JSON)
                .log().body()
                .post(buildUrl("/gameplay/move/" + s))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/final_move_template.json")
                .replace("{round}", "5")
                .replace("{field}", "XXXOO6789")
                .replace("{crosses}", "111000000")
                .replace("{zeroes}", "000110000")
                .replace("{next}", "ZERO")
                .replace("{winner}", "CROSS")
                .replace("{host}", "CROSS")
                .replace("{guest}", "ZERO")
                ;

        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    @Test
    void testFullGameplayWinByZero() {
        String s = initSession();
        makeCrossMove(1, s);
        makeZeroMove(4, s);
        makeCrossMove(2, s);
        makeZeroMove(5, s);
        makeCrossMove(7, s);

        String request = fromFile("/request/move_template.json")
                .replace("{moveBy}", "ZERO")
                .replace("{moveTo}", "6");

        String actual = given()
                .body(request)
                .contentType(ContentType.JSON)
                .log().body()
                .post(buildUrl("/gameplay/move/" + s))
                .then()
                .log().body()
                .statusCode(200)
                .extract().response().asString();

        assertNotNull(actual);
        String expected = fromFile("/response/final_move_template.json")
                .replace("{round}", "6")
                .replace("{field}", "XX3OOOX89")
                .replace("{crosses}", "110000100")
                .replace("{zeroes}", "000111000")
                .replace("{next}", "CROSS")
                .replace("{winner}", "ZERO")
                .replace("{host}", "CROSS")
                .replace("{guest}", "ZERO")
                ;

        JSONAssert.assertEquals(expected, actual, GAME_STATE_COMPARATOR);
    }

    private String initSession() {
        return initSession(null);
    }

    private String initSession(String initBy) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"side\": \"%s\"}".formatted(initBy))
                .log().body()
                .post(buildUrl("/gameplay/init"))
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("session");
    }

    void makeCrossMove(Integer moveTo, String session) {
        makeMove(Actor.CROSS, moveTo, session);
    }

    void makeZeroMove(Integer moveTo, String session) {
        makeMove(Actor.ZERO, moveTo, session);
    }

    void makeMove(Actor moveBy, Integer moveTo, String session) {
        String crossRequest = fromFile("/request/move_template.json")
                .replace("{moveBy}", moveBy.toString())
                .replace("{moveTo}", moveTo.toString());

        given()
                .header("X-SESSION-ID", session)
                .body(crossRequest)
                .contentType(ContentType.JSON)
                .post(buildUrl("/gameplay/move/" + session))
                .then()
                .statusCode(200);
    }

}
