package dev.bakulin.ticktacktoe.client;

import dev.bakulin.ticktacktoe.client.model.GameResponse;
import dev.bakulin.ticktacktoe.client.model.InitRequest;
import dev.bakulin.ticktacktoe.client.model.MoveRequest;
import dev.bakulin.ticktacktoe.client.model.Side;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class GameServerClient {

    private static final String SESSION_HEADER = "X-SESSION-ID";

    final RestTemplate rest;
    final String host;
    final Integer port;

    public GameServerClient(RestTemplate rest, String host, Integer port) {
        this.rest = rest;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public GameResponse initGame() {
        return initGameAs(Side.CROSS);
    }

    public GameResponse initGameAs(Side side) {
        return rest.postForObject(urlTo("/gameplay/init"), new InitRequest().setSide(side), GameResponse.class);
    }

    public GameResponse makeMove(String session, MoveRequest request) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SESSION_HEADER, session);
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<GameResponse> responseEntity = rest.postForEntity(urlTo("/gameplay/move"), requestHttpEntity, GameResponse.class);

        return responseEntity.getBody();
    }

    public GameResponse getState(String session) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SESSION_HEADER, session);
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<GameResponse> responseEntity = rest.exchange(urlTo("/gameplay/state"), HttpMethod.GET, requestHttpEntity, GameResponse.class);

        return responseEntity.getBody();
    }

    public String getField(String session) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SESSION_HEADER, session);
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = rest.postForEntity(urlTo("/gameplay/state"), requestHttpEntity, String.class);

        return responseEntity.getBody();
    }

    public String getRunningSessions() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> responseEntity = rest.exchange(urlTo("/gameplay/list"), HttpMethod.GET, requestHttpEntity, String.class);
        return responseEntity.getBody();
    }

    private String urlTo(String path) {
        return "http://%s:%d%s".formatted(host, port, path);
    }

}
